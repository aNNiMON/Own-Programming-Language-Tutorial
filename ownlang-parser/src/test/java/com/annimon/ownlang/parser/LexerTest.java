package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.LexerException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.annimon.ownlang.parser.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author aNNiMON
 */
public class LexerTest {
    
    @Test
    public void testNumbers() {
        String input = "0 3.1415 0xCAFEBABE 0Xf7_d6_c5 #FFFF #";
        List<Token> expList = list(NUMBER, NUMBER, HEX_NUMBER, HEX_NUMBER, HEX_NUMBER);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
        assertEquals("0", result.get(0).getText());
        assertEquals("3.1415", result.get(1).getText());
        assertEquals("CAFEBABE", result.get(2).getText());
        assertEquals("f7d6c5", result.get(3).getText());
    }
    
    @Test
    public void testNumbersError() {
        final String input = "3.14.15 0Xf7_p6_s5";
        assertThrows(LexerException.class, () -> Lexer.tokenize(input));
    }
    
    @Test
    public void testArithmetic() {
        String input = "x = -1 + 2 * 3 % 4 / 5";
        List<Token> expList = list(WORD, EQ, MINUS, NUMBER, PLUS, NUMBER, STAR, NUMBER, PERCENT, NUMBER, SLASH, NUMBER);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
        assertEquals("x", result.get(0).getText());
    }
    
    @Test
    public void testKeywords() {
        String input = "if else while for include";
        List<Token> expList = list(IF, ELSE, WHILE, FOR, INCLUDE);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }
    
    @Test
    public void testWord() {
        String input = "if bool include \"text\n\ntext\"";
        List<Token> expList = list(IF, WORD, INCLUDE, TEXT);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }
    
    @Test
    public void testString() {
        String input = "\"1\\\"2\"";
        List<Token> expList = list(TEXT);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
        assertEquals("1\"2", result.get(0).getText());
    }
    
    @Test
    public void testEmptyString() {
        String input = "\"\"";
        List<Token> expList = list(TEXT);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
        assertEquals("", result.get(0).getText());
    }
    
    @Test
    public void testStringError() {
        String input = "\"1\"\"";
        List<Token> expList = list(TEXT);
        assertThrows(LexerException.class, () -> {
            List<Token> result = Lexer.tokenize(input);
            assertTokens(expList, result);
            assertEquals("1", result.get(0).getText());
        });
    }
    
    @Test
    public void testOperators() {
        String input = "=+-*/%<>!&|";
        List<Token> expList = list(EQ, PLUS, MINUS, STAR, SLASH, PERCENT, LT, GT, EXCL, AMP, BAR);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }
    
    @Test
    public void testOperators2Char() {
        String input = "== != <= >= && || ==+ >=- ->";
        List<Token> expList = list(EQEQ, EXCLEQ, LTEQ, GTEQ, AMPAMP, BARBAR,
                EQEQ, PLUS,   GTEQ, MINUS,  MINUS, GT);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }
    
    @Test
    public void testComments() {
        String input = "// 1234 \n /* */ 123 /* \n 12345 \n\n\n */";
        List<Token> expList = list(NUMBER);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
        assertEquals("123", result.get(0).getText());
    }
    
    @Test
    public void testComments2() {
        String input = "// /* 1234 \n */";
        List<Token> expList = list(STAR, SLASH);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }
    
    @Test
    public void testCommentsError() {
        final String input = "/* 1234 \n";
        assertThrows(LexerException.class, () -> Lexer.tokenize(input));
    }

    @Test
    public void testExtendedWordError() {
        final String input = "` 1234";
        assertThrows(LexerException.class, () -> Lexer.tokenize(input));
    }

    @Test
    public void testUnicodeCharacterIdentifier() {
        String input = "€ = 1";
        List<Token> expList = list(EQ, NUMBER);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }

    @Test
    public void testUnicodeCharacterExtendedWordIdentifier() {
        String input = "`€` = 1";
        List<Token> expList = list(WORD, EQ, NUMBER);
        List<Token> result = Lexer.tokenize(input);
        assertTokens(expList, result);
    }

    @Test
    public void testUnicodeCharacterEOF() {
        String input = "€";
        assertTrue(Lexer.tokenize(input).isEmpty());
    }
    
    private static void assertTokens(List<Token> expList, List<Token> result) {
        final int length = expList.size();
        assertEquals(length, result.size());
        for (int i = 0; i < length; i++) {
            assertEquals(expList.get(i).getType(), result.get(i).getType());
        }
    }
    
    private static List<Token> list(TokenType... types) {
        final List<Token> list = new ArrayList<Token>();
        for (TokenType t : types) {
            list.add(token(t));
        }
        return list;
    }
    
    private static Token token(TokenType type) {
        return token(type, "", 0, 0);
    }
    
    private static Token token(TokenType type, String text, int row, int col) {
        return new Token(type, text, row, col);
    }
    
}
