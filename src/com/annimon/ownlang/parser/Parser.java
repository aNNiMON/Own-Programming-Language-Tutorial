package com.annimon.ownlang.parser;

import com.annimon.ownlang.parser.ast.BinaryExpression;
import com.annimon.ownlang.parser.ast.Expression;
import com.annimon.ownlang.parser.ast.NumberExpression;
import com.annimon.ownlang.parser.ast.UnaryExpression;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class Parser {
    
    private static final Token EOF = new Token(TokenType.EOF, "");

    private final List<Token> tokens;
    private final int size;
    
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
    }
    
    public List<Expression> parse() {
        final List<Expression> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            result.add(expression());
        }
        return result;
    }
    
    private Expression expression() {
        return additive();
    }
    
    private Expression additive() {
        Expression result = multiplicative();
        
        while (true) {
            if (match(TokenType.PLUS)) {
                result = new BinaryExpression('+', result, multiplicative());
                continue;
            }
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression('-', result, multiplicative());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression multiplicative() {
        Expression result = unary();
        
        while (true) {
            // 2 * 6 / 3 
            if (match(TokenType.STAR)) {
                result = new BinaryExpression('*', result, unary());
                continue;
            }
            if (match(TokenType.SLASH)) {
                result = new BinaryExpression('/', result, unary());
                continue;
            }
            break;
        }
        
        return result;
    }
    
    private Expression unary() {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression('-', primary());
        }
        if (match(TokenType.PLUS)) {
            return primary();
        }
        return primary();
    }
    
    private Expression primary() {
        final Token current = get(0);
        if (match(TokenType.NUMBER)) {
            return new NumberExpression(Double.parseDouble(current.getText()));
        }
        if (match(TokenType.HEX_NUMBER)) {
            return new NumberExpression(Long.parseLong(current.getText(), 16));
        }
        if (match(TokenType.LPAREN)) {
            Expression result = expression();
            match(TokenType.RPAREN);
            return result;
        }
        throw new RuntimeException("Unknown expression");
    }
    
    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }
    
    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= size) return EOF;
        return tokens.get(position);
    }
}
