package com.annimon.ownlang;

import com.annimon.ownlang.lib.CallStack;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.visitors.AssignValidator;
import com.annimon.ownlang.parser.visitors.FunctionAdder;
import com.annimon.ownlang.parser.visitors.VariablePrinter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            run(readFile("program.own"), true, true);
            return;
        }
        
        boolean showTokens = false, showAst = false;
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
                    
                case "-f":
                case "--file":
                    if (i + 1 < args.length)  {
                        input = readFile(args[i + 1]);
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
        run(input, showTokens, showAst);
    }
        
    private static String readFile(String file) throws IOException {
        return new String( Files.readAllBytes(Paths.get(file)), "UTF-8");
    }
    
    private static void run(String input, boolean showTokens, boolean showAst) {
        final List<Token> tokens = new Lexer(input).tokenize();
        if (showTokens) {
            for (int i = 0; i < tokens.size(); i++) {
                System.out.println(i + " " + tokens.get(i));
            }
        }
        
        final Statement program = new Parser(tokens).parse();
        if (showAst) {
            System.out.println(program.toString());
        }
        program.accept(new FunctionAdder());
//        program.accept(new VariablePrinter());
        program.accept(new AssignValidator());
        try {
            program.execute();
        } catch (Exception ex) {
            handleException(Thread.currentThread(), ex);
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
