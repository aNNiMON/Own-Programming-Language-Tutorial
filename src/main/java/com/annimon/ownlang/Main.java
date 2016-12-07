package com.annimon.ownlang;

import com.annimon.ownlang.exceptions.StoppedException;
import com.annimon.ownlang.parser.Beautifier;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Linter;
import com.annimon.ownlang.parser.Optimizer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.FunctionAdder;
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
    
    public static final String VERSION = "1.3.0_" + Gen.BUILD_DATE;

    private static String[] ownlangArgs = new String[0];

    public static String[] getOwnlangArgs() {
        return ownlangArgs;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            try {
                final Options options = new Options();
                options.showAst = false;
                options.showTokens = false;
                options.showMeasurements = false;
                options.lintMode = false;
                options.optimizationLevel = 0;
                run(SourceLoader.readSource("program.own"), options);
            } catch (IOException ioe) {
                System.out.println("OwnLang version " + VERSION + "\n\n" +
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
            return;
        }

        final Options options = new Options();
        boolean beautifyMode = false;
        String input = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                case "--showast":
                    options.showAst = true;
                    break;

                case "-b":
                case "--beautify":
                    beautifyMode = true;
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
        if (beautifyMode) {
            System.out.println(Beautifier.beautify(input));
            return;
        }
        run(input, options);
    }

    private static void createOwnLangArgs(String[] javaArgs, int index) {
        if (index >= javaArgs.length) return;
        ownlangArgs = new String[javaArgs.length - index];
        System.arraycopy(javaArgs, index, ownlangArgs, 0, ownlangArgs.length);
    }
        
    private static void run(String input, Options options) {
        options.validate();
        final TimeMeasurement measurement = new TimeMeasurement();
        measurement.start("Tokenize time");
        final List<Token> tokens = Lexer.tokenize(input);
        measurement.stop("Tokenize time");
        if (options.showTokens) {
            for (int i = 0; i < tokens.size(); i++) {
                System.out.println(i + " " + tokens.get(i));
            }
        }
        
        measurement.start("Parse time");
        final Parser parser = new Parser(tokens);
        final Statement parsedProgram = parser.parse();
        measurement.stop("Parse time");
        if (options.showAst) {
            System.out.println(parsedProgram.toString());
        }
        if (parser.getParseErrors().hasErrors()) {
            System.out.println(parser.getParseErrors());
            return;
        }
        if (options.lintMode) {
            Linter.lint(parsedProgram);
            return;
        }
        final Statement program;
        if (options.optimizationLevel > 0) {
            measurement.start("Optimization time");
            program = Optimizer.optimize(parsedProgram, options.optimizationLevel, options.showAst);
            measurement.stop("Optimization time");
            if (options.showAst) {
                System.out.println(program.toString());
            }
        } else {
            program = parsedProgram;
        }
        program.accept(new FunctionAdder());
        try {
            measurement.start("Execution time");
            program.execute();
        } catch (StoppedException ex) {
            // skip
        } catch (Exception ex) {
            Console.handleException(Thread.currentThread(), ex);
        } finally {
            if (options.showMeasurements) {
                measurement.stop("Execution time");
                System.out.println("======================");
                System.out.println(measurement.summary(TimeUnit.MILLISECONDS, true));
            }
        }
    }

    private static class Options {
        boolean showTokens, showAst, showMeasurements;
        boolean lintMode;
        int optimizationLevel;

        public Options() {
            showTokens = false;
            showAst = false;
            showMeasurements = false;
            lintMode = false;
            optimizationLevel = 0;
        }

        public void validate() {
            if (lintMode == true) {
                showTokens = false;
                showAst = false;
                showMeasurements = false;
                optimizationLevel = 0;
            }
        }
    }
}
