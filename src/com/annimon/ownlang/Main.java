package com.annimon.ownlang;

import com.annimon.ownlang.parser.Lexer;
import com.annimon.ownlang.parser.Parser;
import com.annimon.ownlang.parser.Token;
import com.annimon.ownlang.parser.ast.Expression;
import java.util.List;

/**
 * @author aNNiMON
 */
public final class Main {

    public static void main(String[] args) {
        final String input1 = "2 + 2";
        final String input2 = "(2 + 2) * #f";
        final List<Token> tokens = new Lexer(input2).tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        
        final List<Expression> expressions = new Parser(tokens).parse();
        for (Expression expr : expressions) {
            System.out.println(expr + " = " + expr.eval());
        }
    }
}
