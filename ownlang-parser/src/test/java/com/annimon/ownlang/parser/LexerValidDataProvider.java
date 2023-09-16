package com.annimon.ownlang.parser;

import org.junit.jupiter.params.provider.Arguments;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import static com.annimon.ownlang.parser.TokenType.*;

public class LexerValidDataProvider {

    public static Stream<Arguments> getAll() {
        final var result = new ArrayList<Arguments>();
        result.addAll(numbers());
        result.addAll(keywords());
        result.addAll(words());
        result.addAll(operators());
        result.addAll(comments());
        result.addAll(other());
        result.addAll(notSupported());
        return result.stream();
    }

    private static List<Arguments> numbers() {
        return List.of(
                Arguments.of("Numbers",
                        "12 7.8 90000000 10.03",
                        List.of(NUMBER, NUMBER, NUMBER, NUMBER)),
                Arguments.of("Float notation",
                        "7e8",
                        List.of(NUMBER)),
                Arguments.of("Hex numbers",
                        "#FF 0xCA 0x12fb 0xFF",
                        List.of(HEX_NUMBER, HEX_NUMBER, HEX_NUMBER, HEX_NUMBER))
        );
    }

    private static List<Arguments> keywords() {
        return List.of(
                Arguments.of("Keywords",
                        "if else while for include",
                        List.of(IF, ELSE, WHILE, FOR, INCLUDE))
        );
    }

    private static List<Arguments> words() {
        return List.of(
                Arguments.of("Word",
                        "if bool include \"text\n\ntext\"",
                        List.of(IF, WORD, INCLUDE, TEXT)),
                Arguments.of("Extended word identifier",
                        "`€` = 1",
                        List.of(WORD, EQ, NUMBER))
        );
    }

    private static List<Arguments> operators() {
        return List.of(
                Arguments.of("Operators",
                        "=+-*/%<>!&|",
                        List.of(EQ, PLUS, MINUS, STAR, SLASH, PERCENT, LT, GT, EXCL, AMP, BAR)),
                Arguments.of("Operators 2 characters",
                        "== != <= >= && || ==+ >=- ->",
                        List.of(EQEQ, EXCLEQ, LTEQ, GTEQ,  AMPAMP, BARBAR,
                                EQEQ, PLUS,   GTEQ, MINUS, MINUS,  GT))
        );
    }

    private static List<Arguments> comments() {
        return List.of(
                Arguments.of("Comments",
                        "// /* 1234 \n */",
                        List.of(STAR, SLASH))
        );
    }

    private static List<Arguments> other() {
        return List.of(
                Arguments.of("Arithmetic",
                        "x = -1 + 2 * 3 % 4 / 5",
                        List.of(WORD, EQ, MINUS, NUMBER, PLUS, NUMBER, STAR, NUMBER, PERCENT, NUMBER, SLASH, NUMBER))
        );
    }

    private static List<Arguments> notSupported() {
        return List.of(
                Arguments.of("Float hex numbers",
                        "0Xf7p6",
                        List.of(HEX_NUMBER, WORD))
        );
    }
}
