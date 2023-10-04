package com.annimon.ownlang;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.StoppedException;
import com.annimon.ownlang.parser.Beautifier;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.error.ParseErrorsFormatterStage;
import com.annimon.ownlang.parser.linters.LinterStage;
import com.annimon.ownlang.parser.optimization.OptimizationStage;
import com.annimon.ownlang.stages.*;
import com.annimon.ownlang.utils.Repl;
import com.annimon.ownlang.utils.Sandbox;
import com.annimon.ownlang.utils.TimeMeasurement;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            try {
                runDefault();
            } catch (IOException ioe) {
                printUsage();
            }
            return;
        }

        final RunOptions options = new RunOptions();
        String input = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                case "--showast":
                    options.showAst = true;
                    break;

                case "-b":
                case "--beautify":
                    options.beautifyMode = true;
                    break;

                case "-t":
                case "--showtokens":
                    options.showTokens = true;
                    break;

                case "-m":
                case "--showtime":
                    options.showMeasurements = true;
                    break;

                case "-o":
                case "--optimize":
                    if (i + 1 < args.length) {
                        try {
                            options.optimizationLevel = Integer.parseInt(args[i + 1]);
                        } catch (NumberFormatException nfe) {
                            options.optimizationLevel = 2;
                        }
                        i++;
                    } else {
                        options.optimizationLevel = 2;
                    }
                    break;

                case "-r":
                case "--repl":
                    Repl.main(new String[0]);
                    return;

                case "-l":
                case "--lint":
                    options.lintMode = true;
                    return;

                case "-f":
                case "--file":
                    if (i + 1 < args.length)  {
                        input = SourceLoader.readSource(args[i + 1]);
                        createOwnLangArgs(args, i + 2);
                        i++;
                    }
                    break;

                case "--sandbox":
                    createOwnLangArgs(args, i + 1);
                    final String[] ownlangArgs = Shared.getOwnlangArgs();
                    String[] newArgs = new String[ownlangArgs.length];
                    System.arraycopy(ownlangArgs, 0, newArgs, 0, ownlangArgs.length);
                    Sandbox.main(newArgs);
                    return;

                default:
                    if (input == null) {
                        input = args[i];
                        createOwnLangArgs(args, i + 1);
                    }
                    break;
            }
        }
        if (input == null) {
            throw new IllegalArgumentException("Empty input");
        }
        if (options.beautifyMode) {
            System.out.println(Beautifier.beautify(input));
            return;
        }
        run(input, options);
    }

    private static void runDefault() throws IOException {
        final RunOptions options = new RunOptions();
        run(SourceLoader.readSource("program.own"), options);
    }

    private static void printUsage() {
        System.out.println("OwnLang version " + Version.VERSION + "\n\n" +
                "Usage: ownlang [options]\n" +
                "  options:\n" +
                "      -f, --file [input]  Run program file. Required.\n" +
                "      -r, --repl          Enter to a REPL mode\n" +
                "      -l, --lint          Find bugs in code\n" +
                "      -o N, --optimize N  Perform optimization with N passes\n" +
                "      -b, --beautify      Beautify source code\n" +
                "      -a, --showast       Show AST of program\n" +
                "      -t, --showtokens    Show lexical tokens\n" +
                "      -m, --showtime      Show elapsed time of parsing and execution");
    }

    private static void createOwnLangArgs(String[] javaArgs, int index) {
        if (index >= javaArgs.length) return;
        final String[] ownlangArgs = new String[javaArgs.length - index];
        System.arraycopy(javaArgs, index, ownlangArgs, 0, ownlangArgs.length);
        Shared.setOwnlangArgs(ownlangArgs);
    }

    private static void run(String input, RunOptions options) {
        final var measurement = new TimeMeasurement();
        final var scopedStages = new ScopedStageFactory(measurement::start, measurement::stop);

        final var stagesData = new StagesDataMap();
        stagesData.put(SourceLoaderStage.TAG_SOURCE, input);
        try {
            scopedStages.create("Lexer", new LexerStage())
                    .then(scopedStages.create("Parser", new ParserStage()))
                    .thenConditional(options.optimizationLevel > 0,
                            scopedStages.create("Optimization",
                                    new OptimizationStage(options.optimizationLevel, options.showAst)))
                    .thenConditional(options.lintMode,
                            scopedStages.create("Linter", new LinterStage()))
                    .then(scopedStages.create("Function adding", new FunctionAddingStage()))
                    .then(scopedStages.create("Execution", new ExecutionStage()))
                    .perform(stagesData, input);
        } catch (OwnLangParserException ex) {
            final var error = new ParseErrorsFormatterStage()
                    .perform(stagesData, ex.getParseErrors());
            System.err.println(error);
        } catch (StoppedException ex) {
            // skip
        } catch (Exception ex) {
            Console.handleException(stagesData, Thread.currentThread(), ex);
        } finally {
            if (options.showTokens) {
                final List<Token> tokens = stagesData.get(LexerStage.TAG_TOKENS);
                int i = 0;
                for (Token token : tokens) {
                    System.out.println(i++ + " " + token);
                }
            }
            if (options.showAst) {
                Statement program = stagesData.get(ParserStage.TAG_PROGRAM);
                System.out.println(program);
                System.out.println(stagesData.getOrDefault(OptimizationStage.TAG_OPTIMIZATION_SUMMARY, ""));
            }
            if (options.showMeasurements) {
                System.out.println("======================");
                System.out.println(measurement.summary(TimeUnit.MILLISECONDS, true));
            }
        }
    }

    private static class RunOptions {
        boolean showTokens, showAst, showMeasurements;
        boolean lintMode;
        boolean beautifyMode;
        int optimizationLevel;

        RunOptions() {
            showTokens = false;
            showAst = false;
            showMeasurements = false;
            lintMode = false;
            beautifyMode = false;
            optimizationLevel = 0;
        }
    }
}
