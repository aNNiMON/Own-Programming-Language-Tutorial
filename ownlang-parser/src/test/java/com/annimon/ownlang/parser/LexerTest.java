package com.annimon.ownlang.parser;

import com.annimon.ownlang.exceptions.OwnLangParserException;
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
                .add(Arguments.of("Empty float exponent", "3e"))
                .add(Arguments.of("Float number too small", "3e-00009000"))
                .add(Arguments.of("Float number too large", "3e+00009000"))
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
    void testNumbers() {
        String input = "0 800L 3.1415 0xCAFEBABE 0Xf7_d6_c5 #FFFF 0x7FL";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, NUMBER, LONG_NUMBER, DECIMAL_NUMBER, HEX_NUMBER, HEX_NUMBER, HEX_NUMBER, HEX_LONG_NUMBER);
        assertThat(result)
                .extracting(Token::text)
                .containsExactly("0", "800", "3.1415", "CAFEBABE", "f7d6c5", "FFFF", "7F");
    }

    @Test
    void testDecimalNumbersExponent() {
        String input = "4e+7 0.3E-19 2e0 5e0000000000000200 5E-000000089";
        List<Token> result = Lexer.tokenize(input);
        assertThat(result)
                .allMatch(t -> t.type().equals(DECIMAL_NUMBER))
                .extracting(Token::text)
                .containsExactly("4e7", "0.3E-19", "2e0", "5e200", "5E-89");
    }
    
    @Test
    void testString() {
        String input = "\"1\\\"2\"";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("1\"2", result.get(0).text());
    }

    @Test
    void testEscapeString() {
        String input = """
                "\\\\/\\\\"
                """.stripIndent();
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("\\/\\", result.get(0).text());
    }
    
    @Test
    void testEmptyString() {
        String input = "\"\"";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, TEXT);
        assertEquals("", result.get(0).text());
    }
    
    @Test
    void testComments() {
        String input = "// 1234 \n /* */ 123 /* \n 12345 \n\n\n */";
        List<Token> result = Lexer.tokenize(input);
        assertTokens(result, NUMBER);
        assertEquals("123", result.get(0).text());
    }

    @ParameterizedTest
    @MethodSource("validData")
    void testValidInput(String name, String input, List<TokenType> tokenTypes) throws IOException {
        List<Token> result = Lexer.tokenize(input);
        assertThat(result)
                .hasSize(tokenTypes.size())
                .extracting(Token::type)
                .containsAll(tokenTypes);
    }

    @ParameterizedTest
    @MethodSource("invalidData")
    void testInvalidInput(String name, String input) throws IOException {
        assertThatThrownBy(() -> Lexer.tokenize(input))
                .isInstanceOf(OwnLangParserException.class);
    }
    
    private static void assertTokens(List<Token> result, TokenType... tokenTypes) {
        assertThat(result)
                .hasSize(tokenTypes.length)
                .extracting(Token::type)
                .containsExactly(tokenTypes);
    }
}
