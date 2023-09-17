package com.annimon.ownlang.exceptions;

/**
 * Base type for all runtime exceptions
 */
public abstract class OwnLangRuntimeException extends RuntimeException {

    public OwnLangRuntimeException() {
        super();
    }

    public OwnLangRuntimeException(String message) {
        super(message);
    }
}