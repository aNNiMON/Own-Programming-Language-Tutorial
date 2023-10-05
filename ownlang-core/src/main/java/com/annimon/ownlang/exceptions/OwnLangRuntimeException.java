package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocatedError;

/**
 * Base type for all runtime exceptions
 */
public class OwnLangRuntimeException extends RuntimeException implements SourceLocatedError {

    private final Range range;

    public OwnLangRuntimeException() {
        super();
        this.range = null;
    }

    public OwnLangRuntimeException(Exception ex) {
        super(ex);
        this.range = null;
    }

    public OwnLangRuntimeException(String message) {
        this(message, (Range) null);
    }

    public OwnLangRuntimeException(String message, Range range) {
        super(message);
        this.range = range;
    }

    public OwnLangRuntimeException(String message, Throwable ex) {
        super(message, ex);
        this.range = null;
    }

    @Override
    public Range getRange() {
        return range;
    }
}