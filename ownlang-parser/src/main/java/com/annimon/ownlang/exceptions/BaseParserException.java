package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

/**
 * Base type for all lexer and parser exceptions
 */
public abstract class BaseParserException extends RuntimeException {

    private final Range range;

    public BaseParserException(String message, Range range) {
        super(message);
        this.range = range;
    }

    public Range getRange() {
        return range;
    }
}
