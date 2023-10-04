package com.annimon.ownlang.util;

public record Pos(int row, int col) {
    public static final Pos UNKNOWN = new Pos(-1, -1);
    public static final Pos ZERO = new Pos(0, 0);

    public Pos normalize() {
        return new Pos(Math.max(0, row - 1), Math.max(0, col - 1));
    }

    public String format() {
        return "[" + row + ":" + col + "]";
    }
}
