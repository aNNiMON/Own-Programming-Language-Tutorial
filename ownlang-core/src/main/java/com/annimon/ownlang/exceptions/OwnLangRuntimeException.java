package com.annimon.ownlang.exceptions;

/**
 * Base type for all runtime exceptions
 */
public class OwnLangRuntimeException extends RuntimeException {

    public OwnLangRuntimeException() {
        super();
    }

    public OwnLangRuntimeException(String message) {
        super(message);
    }

    public OwnLangRuntimeException(String message, Throwable ex) {
        super(message, ex);
    }
}