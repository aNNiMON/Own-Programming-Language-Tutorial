package com.annimon.ownlang.parser.error;

import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocatedError;

public record ParseError(
        String message,
        Range range,
        StackTraceElement[] stackTraceElements
) implements SourceLocatedError {

    public ParseError(String message, Range range) {
        this(message, range, new StackTraceElement[0]);
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return stackTraceElements;
    }

    @Override
    public String toString() {
        return "Error on line " + range().start().row() + ": " + message;
    }
}
