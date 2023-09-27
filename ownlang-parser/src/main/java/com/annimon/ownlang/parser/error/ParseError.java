package com.annimon.ownlang.parser.error;

import com.annimon.ownlang.parser.Range;
import java.util.Collections;
import java.util.List;

public record ParseError(String message, Range range, List<StackTraceElement> stackTraceElements) {
    public ParseError(String message, Range range) {
        this(message, range, Collections.emptyList());
    }

    public boolean hasStackTrace() {
        return !stackTraceElements.isEmpty();
    }

    @Override
    public String toString() {
        return "Error on line " + range().start().row() + ": " + message;
    }
}
