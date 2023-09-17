package com.annimon.ownlang.exceptions;

/**
 * Base type for all lexer and parser exceptions
 */
public abstract class OwnLangParserException extends RuntimeException {

    public OwnLangParserException() {
        super();
    }

    public OwnLangParserException(String message) {
        super(message);
    }
}
