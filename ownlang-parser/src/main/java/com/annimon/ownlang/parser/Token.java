package com.annimon.ownlang.parser;

/**
 * @author aNNiMON
 */
public record Token(TokenType type, String text, Pos pos) {

    @Override
    public String toString() {
        return type.name() + " " + pos().format() + " " + text;
    }
}
