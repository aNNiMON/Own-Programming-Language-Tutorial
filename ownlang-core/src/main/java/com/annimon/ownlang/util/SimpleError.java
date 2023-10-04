package com.annimon.ownlang.util;

public record SimpleError(String message) implements SourceLocatedError {
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
