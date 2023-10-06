package com.annimon.ownlang.util;

public record SimpleError(String message, Range range) implements SourceLocatedError {
    public SimpleError(String message) {
        this(message, null);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public Range getRange() {
        return range;
    }
}
