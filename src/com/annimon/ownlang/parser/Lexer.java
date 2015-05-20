package com.annimon.ownlang.parser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class Lexer {
    
    private static final String OPERATOR_CHARS = "+-*/()";
    private static final TokenType[] OPERATOR_TOKENS = {
        TokenType.PLUS, TokenType.MINUS,
        TokenType.STAR, TokenType.SLASH,
        TokenType.LPAREN, TokenType.RPAREN,
    };

    private final String input;
    private final int length;
    
    private final List<Token> tokens;
    
    private int pos;

    public Lexer(String input) {
        this.input = input;
        length = input.length();
        
        tokens = new ArrayList<>();
    }
    
    public List<Token> tokenize() {
        while (pos < length) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (Character.isLetter(current)) tokenizeWord();
            else if (current == '#') {
                next();
                tokenizeHexNumber();
            }
            else if (OPERATOR_CHARS.indexOf(current) != -1) {
                tokenizeOperator();
            } else {
                // whitespaces
                next();
            }
        }
        return tokens;
    }
    
    private void tokenizeNumber() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (true) {
            if (current == '.') {
                if (buffer.indexOf(".") != -1) throw new RuntimeException("Invalid float number");
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.NUMBER, buffer.toString());
    }
    
    private void tokenizeHexNumber() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (Character.isDigit(current) || isHexNumber(current)) {
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.HEX_NUMBER, buffer.toString());
    }

    private static boolean isHexNumber(char current) {
        return "abcdef".indexOf(Character.toLowerCase(current)) != -1;
    }
    
    private void tokenizeOperator() {
        final int position = OPERATOR_CHARS.indexOf(peek(0));
        addToken(OPERATOR_TOKENS[position]);
        next();
    }
    
    private void tokenizeWord() {
        final StringBuilder buffer = new StringBuilder();
        char current = peek(0);
        while (true) {
            if (!Character.isLetterOrDigit(current) && (current != '_')  && (current != '$')) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        addToken(TokenType.WORD, buffer.toString());
    }
    
    private char next() {
        pos++;
        return peek(0);
    }
    
    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if (position >= length) return '\0';
        return input.charAt(position);
    }
    
    private void addToken(TokenType type) {
        addToken(type, "");
    }
    
    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text));
    }
}
