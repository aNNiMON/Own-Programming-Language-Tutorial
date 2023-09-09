package com.annimon.ownlang.parser;

public record Pos(int row, int col) {

    public Pos normalize() {
        return new Pos(Math.max(0, row - 1), Math.max(0, col - 1));
    }

    public String format() {
        return "[" + row + ":" + col + "]";
    }
}
