package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocatedError;

record LinterResult(Severity severity, String message, Range range) implements SourceLocatedError {

    enum Severity { ERROR, WARNING }

    static LinterResult warning(String message) {
        return new LinterResult(Severity.WARNING, message);
    }

    static LinterResult warning(String message, Range range) {
        return new LinterResult(Severity.WARNING, message, range);
    }

    static LinterResult error(String message) {
        return new LinterResult(Severity.ERROR, message);
    }

    static LinterResult error(String message, Range range) {
        return new LinterResult(Severity.ERROR, message, range);
    }

    LinterResult(Severity severity, String message) {
        this(severity, message, null);
    }

    public boolean isError() {
        return severity == Severity.ERROR;
    }

    public boolean isWarning() {
        return severity == Severity.WARNING;
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
    public String toString() {
        return severity.name() + ": " + message;
    }
}
