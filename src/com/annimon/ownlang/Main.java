package com.annimon.ownlang;

import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Statement;
import java.util.List;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) {
        final String input1 = "word = 2 + 2\nword2 = PI + word";
//        final String input2 = "(GOLDEN_RATIO + 2) * #f";
        final String input2 = "GOLDEN_RATIO";
        final List<Token> tokens = new Lexer(input1).tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        
        final List<Statement> statements = new Parser(tokens).parse();
        for (Statement statement : statements) {
            System.out.println(statement);
        }
        for (Statement statement : statements) {
            statement.execute();
        }
        System.out.printf("%s = %f\n", "word", Variables.get("word"));
        System.out.printf("%s = %f\n", "word2", Variables.get("word2"));
    }
}
