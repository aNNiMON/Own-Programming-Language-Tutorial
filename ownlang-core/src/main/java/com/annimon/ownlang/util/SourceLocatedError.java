package com.annimon.ownlang.util;

public interface SourceLocatedError extends SourceLocation {

    String getMessage();

    default StackTraceElement[] getStackTrace() {
        return new StackTraceElement[0];
    }

    default boolean hasStackTrace() {
        return !stackTraceIsEmpty();
    }

    private boolean stackTraceIsEmpty() {
        final var st = getStackTrace();
        return st == null || st.length == 0;
    }
}
