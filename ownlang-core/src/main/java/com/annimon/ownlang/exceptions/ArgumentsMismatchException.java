package com.annimon.ownlang.exceptions;

public final class ArgumentsMismatchException extends OwnLangRuntimeException {

    public ArgumentsMismatchException() {
    }

    public ArgumentsMismatchException(String message) {
        super(message);
    }
}
