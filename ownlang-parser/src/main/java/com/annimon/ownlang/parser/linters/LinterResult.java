package com.annimon.ownlang.parser.linters;

record LinterResult(Severity severity, String message) {

    enum Severity { ERROR, WARNING }

    @Override
    public String toString() {
        return severity.name() + ": " + message;
    }
}
