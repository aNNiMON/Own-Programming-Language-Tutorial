package com.annimon.ownlang;

import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.exceptions.StoppedException;
import com.annimon.ownlang.parser.BeautifierStage;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.error.ParseErrorsFormatterStage;
import com.annimon.ownlang.parser.linters.LinterStage;
import com.annimon.ownlang.parser.optimization.OptimizationStage;
import com.annimon.ownlang.stages.*;
import com.annimon.ownlang.util.input.SourceLoaderStage;
import com.annimon.ownlang.utils.Repl;
import com.annimon.ownlang.utils.Sandbox;
import com.annimon.ownlang.utils.TimeMeasurement;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        final RunOptions options = new RunOptions();
        if (args.length == 0 && (options.detectDefaultProgramPath() == null)) {
            printUsage();
            return;
        }

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
                    final String lintMode = i + 1 < args.length ? args[++i] : LinterStage.Mode.SEMANTIC.name();
                    options.lintMode = switch (lintMode.toLowerCase(Locale.ROOT)) {
                        case "none" -> LinterStage.Mode.NONE;
                        case "full" -> LinterStage.Mode.FULL;
                        default -> LinterStage.Mode.SEMANTIC;
                    };
                    break;

                case "-f":
                case "--file":
                    if (i + 1 < args.length) {
                        options.programPath = args[i + 1];
                        createOwnLangArgs(args, i + 2);
                        i++;
                    }
                    break;

                case "--sandbox":
                    Sandbox.main(createOwnLangArgs(args, i + 1));
                    return;

                case "run":
                    final String scriptName;
                    if (i + 1 < args.length) {
                        scriptName = args[i + 1];
                        createOwnLangArgs(args, i + 2);
                    } else {
                        scriptName = "listScripts";
                    }
                    run(RunOptions.script(scriptName));
                    return;

                default:
                    if (options.programSource == null) {
                        options.programSource = args[i];
                        createOwnLangArgs(args, i + 1);
                    }
                    break;
            }
        }
        if (options.beautifyMode) {
            String result = new SourceLoaderStage()
                    .then(new BeautifierStage())
                    .perform(new StagesDataMap(), options.toInputSource());
            System.out.println(result);
            return;
        }
        run(options);
    }

    private static void printUsage() {
        System.out.println("OwnLang version %s\n\n".formatted(Version.VERSION) + """
                Usage: ownlang [options]
                  options:
                      -f, --file [input]  Run program file. Required.
                      -r, --repl          Enter to a REPL mode
                      -l, --lint <mode>   Find bugs in code. Mode: none, semantic, full
                      -o, --optimize N    Perform optimization with N (0...9) passes
                      -b, --beautify      Beautify source code
                      -a, --showast       Show AST of program
                      -t, --showtokens    Show lexical tokens
                      -m, --showtime      Show elapsed time of parsing and execution
                  ownlang run <scriptName>
                """);
    }

    private static String[] createOwnLangArgs(String[] javaArgs, int index) {
        final String[] ownlangArgs;
        if (index >= javaArgs.length) {
            ownlangArgs = new String[0];
        } else {
            ownlangArgs = new String[javaArgs.length - index];
            System.arraycopy(javaArgs, index, ownlangArgs, 0, ownlangArgs.length);
        }
        Shared.setOwnlangArgs(ownlangArgs);
        return ownlangArgs;
    }

    private static void run(RunOptions options) {
        final var measurement = new TimeMeasurement();
        final var scopedStages = new ScopedStageFactory(measurement::start, measurement::stop);

        final var stagesData = new StagesDataMap();
        try {
            scopedStages.create("Source loader", new SourceLoaderStage())
                    .then(scopedStages.create("Lexer", new LexerStage()))
                    .then(scopedStages.create("Parser", new ParserStage()))
                    .thenConditional(options.optimizationLevel > 0,
                            scopedStages.create("Optimization",
                                    new OptimizationStage(options.optimizationLevel, options.showAst)))
                    .thenConditional(options.linterEnabled(),
                            scopedStages.create("Linter", new LinterStage(options.lintMode)))
                    .then(scopedStages.create("Function adding", new FunctionAddingStage()))
                    .then(scopedStages.create("Execution", new ExecutionStage()))
                    .perform(stagesData, options.toInputSource());
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
                System.out.println("=".repeat(25));
                System.out.println(measurement.summary(TimeUnit.MILLISECONDS, true));
            }
        }
    }
}
