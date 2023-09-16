package com.annimon.ownlang.parser;

/**
 * @author aNNiMON
 */
public record Token(TokenType type, String text, Pos pos) {

    public String shortDescription() {
        return type().name() + " " + text;
    }

    @Override
    public String toString() {
        return type.name() + " " + pos().format() + " " + text;
    }
}
