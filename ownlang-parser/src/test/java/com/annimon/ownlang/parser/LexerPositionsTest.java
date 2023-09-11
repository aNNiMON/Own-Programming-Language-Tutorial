package com.annimon.ownlang.parser;

import org.junit.jupiter.api.Test;
import java.util.List;
import static com.annimon.ownlang.parser.TokenType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class LexerPositionsTest {

    @Test
    void testMultiline() {
        String input = """
                x = 123
                y = "abc"
                """.stripIndent();
        List<Token> result = Lexer.tokenize(input);

        assertThat(result)
                .hasSize(6)
                .extracting(s -> s.pos().row(), s -> s.pos().col(), Token::type)
                .containsExactly(
                        tuple(1, 1, WORD), tuple(1, 3, EQ), tuple(1, 5, NUMBER),
                        tuple(2, 1, WORD), tuple(2, 3, EQ), tuple(2, 5, TEXT)
                );
    }

    @Test
    void testMultilineText() {
        String input = """
                text = "line1
                  line2
                  line3"
                """.stripIndent();
        List<Token> result = Lexer.tokenize(input);

        assertThat(result)
                .hasSize(3)
                .extracting(s -> s.pos().row(), s -> s.pos().col(), Token::type)
                .containsExactly(
                        tuple(1, 1, WORD), tuple(1, 6, EQ), tuple(1, 8, TEXT)
                );
    }
}