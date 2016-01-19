package com.annimon.ownlang.exceptions;

public final class OperationIsNotSupportedException extends RuntimeException {

    public OperationIsNotSupportedException(Object operation) {
        super("Operation " + operation + " is not supported");
    }
}
