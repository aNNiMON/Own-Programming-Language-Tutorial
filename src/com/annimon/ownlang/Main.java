package com.annimon.ownlang;

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
        final String file = "examples/game/agar.own";
        final String input = new String( Files.readAllBytes(Paths.get(file)), "UTF-8");
        final List<Token> tokens = new Lexer(input).tokenize();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(i + " " + tokens.get(i));
        }
//        for (Token token : tokens) {
//            System.out.println(token);
//        }
        
        final Statement program = new Parser(tokens).parse();
        System.out.println(program.toString());
        program.accept(new FunctionAdder());
        program.accept(new VariablePrinter());
        program.accept(new AssignValidator());
        program.execute();
    }
}
