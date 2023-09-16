package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.LexerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import static com.annimon.ownlang.parser.TokenType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author aNNiMON
 */
public class LexerTest {

    public static Stream<Arguments> validData() {
        return LexerValidDataProvider.getAll();
    }

    public static Stream<Arguments> invalidData() {
        return Stream.<Arguments>builder()
                .add(Arguments.of("Wrong float point", "3.14.15"))
                .add(Arguments.of("Wrong HEX number", "0Xf7_p6_s5"))
                .add(Arguments.of("HEX number ends with _", "0Xf7_"))
                .add(Arguments.of("Empty rest of HEX number", "#"))
                .add(Arguments.of("Unicode character identifier", "€ = 1"))
                .add(Arguments.of("Unicode character only", "€"))
                .add(Arguments.of("String error", "\"1\"\""))
                .add(Arguments.of("Multiline comment EOF", "/* 1234 \n"))
                .add(Arguments.of("Extended word EOF", "` 1234"))
                .build();
    }
    
    @Test
    public void testNumbers() {
        String input = "0 3.1415 0xCAFEBABE 0Xf7_d6_c5 #FFFF";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, NUMBER, NUMBER, HEX_NUMBER, HEX_NUMBER, HEX_NUMBER);
        assertThat(result)
                .extracting(Token::text)
                .containsExactly("0", "3.1415", "CAFEBABE", "f7d6c5", "FFFF");
    }
    
    @Test
    public void testString() {
        String input = "\"1\\\"2\"";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("1\"2", result.get(0).text());
    }

    @Test
    public void testEscapeString() {
        String input = """
                "\\\\/\\\\"
                """.stripIndent();
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("\\/\\", result.get(0).text());
    }
    
    @Test
    public void testEmptyString() {
        String input = "\"\"";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("", result.get(0).text());
    }
    
    @Test
    public void testComments() {
        String input = "// 1234 \n /* */ 123 /* \n 12345 \n\n\n */";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, NUMBER);
        assertEquals("123", result.get(0).text());
    }

    @ParameterizedTest
    @MethodSource("validData")
    public void testValidInput(String name, String input, List<TokenType> tokenTypes) throws IOException {
        List<Token> result = Lexer.tokenize(input);
        assertThat(result)
                .hasSize(tokenTypes.size())
                .extracting(Token::type)
                .containsAll(tokenTypes);
    }

    @ParameterizedTest
    @MethodSource("invalidData")
    public void testInvalidInput(String name, String input) throws IOException {
        assertThatThrownBy(() -> Lexer.tokenize(input))
                .isInstanceOf(LexerException.class);
    }
    
    private static void assertTokens(List<Token> result, TokenType... tokenTypes) {
        assertThat(result)
                .hasSize(tokenTypes.length)
                .extracting(Token::type)
                .containsExactly(tokenTypes);
    }
}
