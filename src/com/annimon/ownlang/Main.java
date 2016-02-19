package com.annimon.ownlang;

import com.annimon.ownlang.lib.CallStack;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.SourceLoader;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.AssignValidator;
import com.annimon.ownlang.parser.visitors.FunctionAdder;
import com.annimon.ownlang.parser.visitors.VariablePrinter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            run(SourceLoader.readSource("program.own"), true, true, true);
            return;
        }
        
        boolean showTokens = false, showAst = false, showMeasurements = false;
        String input = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                case "--showast":
                    showAst = true;
                    break;
                    
                case "-t":
                case "--showtokens":
                    showTokens = true;
                    break;
                    
                case "-m":
                case "--showtime":
                    showMeasurements = true;
                    break;
                    
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
//        program.accept(new VariablePrinter());
        program.accept(new AssignValidator());
        try {
            measurement.start("Execution time");
            program.execute();
        } catch (Exception ex) {
            handleException(Thread.currentThread(), ex);
        } finally {
            if (showMeasurements) {
                measurement.stop("Execution time");
                System.out.println("======================");
                System.out.println(measurement.summary(TimeUnit.MILLISECONDS, true));
            }
        }
    }
    
    public static void handleException(Thread thread, Throwable throwable) {
        System.err.printf("%s in %s\n", throwable.getMessage(), thread.getName());
        for (CallStack.CallInfo call : CallStack.getCalls()) {
            System.err.printf("\tat %s\n", call);
        }
        throwable.printStackTrace();
    }
}
