package com.annimon.ownlang.parser;

import java.util.Objects;

public record Range(Pos start, Pos end) {
    public static final Range ZERO = new Range(Pos.ZERO, Pos.ZERO);

    public Range normalize() {
        return new Range(start.normalize(), end.normalize());
    }

    public boolean isEqualPosition() {
        return Objects.equals(start, end);
    }

    public boolean isOnSameLine() {
        return start.row() == end.row();
    }

    public String format() {
        if (isOnSameLine()) {
            return start.format();
        } else {
            return start.format() + "..." + end.format();
        }
    }
}
