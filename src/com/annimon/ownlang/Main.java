package com.annimon.ownlang;

import com.annimon.ownlang.exceptions.LexerException;
import com.annimon.ownlang.parser.Beautifier;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.AssignValidator;
import com.annimon.ownlang.parser.visitors.FunctionAdder;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author aNNiMON
 */
public final class Main {
    
    private static final String VERSION = "1.1.0";

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            try {
                run(SourceLoader.readSource("program.own"), true, true, true);
            } catch (IOException ioe) {
                System.out.println("OwnLang version " + VERSION + "\n\n" +
                        "Usage: ownlang [options]\n" +
                        "  options:\n" +
                        "      -f, --file [input]  Run program file. Required.\n" +
                        "      -r, --repl          Enter to a REPL mode\n" +
                        "      -a, --showast       Show AST of program\n" +
                        "      -t, --showtokens    Show lexical tokens\n" +
                        "      -m, --showtime      Show elapsed time of parsing and execution");
            }
            return;
        }
        
        boolean showTokens = false, showAst = false, showMeasurements = false;
        boolean beautifyMode = false;
        String input = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                case "--showast":
                    showAst = true;
                    break;

                case "-b":
                case "--beautify":
                    beautifyMode = true;
                    break;
                    
                case "-t":
                case "--showtokens":
                    showTokens = true;
                    break;
                    
                case "-m":
                case "--showtime":
                    showMeasurements = true;
                    break;
                    
                case "-r":
                case "--repl":
                    repl();
                    return;
                    
                case "-f":
                case "--file":
                    if (i + 1 < args.length)  {
                        input = SourceLoader.readSource(args[i + 1]);
                        i++;
                    }
                    break;
                
                default:
                    input = args[i];
            }
        }
        if (input == null) {
            throw new IllegalArgumentException("Empty input");
        }
        if (beautifyMode) {
            System.out.println(Beautifier.beautify(input));
            return;
        }
        run(input, showTokens, showAst, showMeasurements);
    }
        
    private static void run(String input, boolean showTokens, boolean showAst, boolean showMeasurements) {
        final TimeMeasurement measurement = new TimeMeasurement();
        measurement.start("Tokenize time");
        final List<Token> tokens = Lexer.tokenize(input);
        measurement.stop("Tokenize time");
        if (showTokens) {
            for (int i = 0; i < tokens.size(); i++) {
                System.out.println(i + " " + tokens.get(i));
            }
        }
        
        measurement.start("Parse time");
        final Parser parser = new Parser(tokens);
        final Statement program = parser.parse();
        measurement.stop("Parse time");
        if (showAst) {
            System.out.println(program.toString());
        }
        if (parser.getParseErrors().hasErrors()) {
            System.out.println(parser.getParseErrors());
            return;
        }
        program.accept(new FunctionAdder());
        program.accept(new AssignValidator());
        try {
            measurement.start("Execution time");
            program.execute();
        } catch (Exception ex) {
            Console.handleException(Thread.currentThread(), ex);
        } finally {
            if (showMeasurements) {
                measurement.stop("Execution time");
                System.out.println("======================");
                System.out.println(measurement.summary(TimeUnit.MILLISECONDS, true));
            }
        }
    }
    
    private static void repl() {
        final StringBuilder buffer = new StringBuilder();
        final Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to OwnLang " + VERSION + " REPL\n"
                + "Type in expressions to have them evaluated.\n"
                + "Type :reset to clear buffer.\n"
                + "Type :exit to exit REPL.");
        while (true) {
            System.out.print((buffer.length() == 0) ? "\n> " : "  ");
            
            if (!scanner.hasNextLine()) break;
            
            final String line = scanner.nextLine();
            if (":exit".equalsIgnoreCase(line)) break;
            if (":reset".equalsIgnoreCase(line)) {
                buffer.setLength(0);
                continue;
            }
            
            buffer.append(line).append(System.lineSeparator());
            try {
                final List<Token> tokens = Lexer.tokenize(buffer.toString());
                final Parser parser = new Parser(tokens);
                final Statement program = parser.parse();
                if (parser.getParseErrors().hasErrors()) {
                    continue;
                }
                program.execute();
            } catch (LexerException lex) {
                continue;
            } catch (Exception ex) {
                Console.handleException(Thread.currentThread(), ex);
            }
            buffer.setLength(0);
        }
        scanner.close();
    }
}
