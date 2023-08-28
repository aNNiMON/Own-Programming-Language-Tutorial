package com.annimon.ownlang.parser;

/**
 * @author aNNiMON
 */
public record Token(TokenType type, String text, int row, int col) {

    public String position() {
        return "[" + row + " " + col + "]";
    }

    @Override
    public String toString() {
        return type.name() + " " + position() + " " + text;
    }
}
