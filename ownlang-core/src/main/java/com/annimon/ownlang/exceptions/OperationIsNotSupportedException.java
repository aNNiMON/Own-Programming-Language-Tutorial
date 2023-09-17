package com.annimon.ownlang.exceptions;

public final class OperationIsNotSupportedException extends OwnLangRuntimeException {

    public OperationIsNotSupportedException(Object operation) {
        super("Operation " + operation + " is not supported");
    }
    
    public OperationIsNotSupportedException(Object operation, String message) {
        super("Operation " + operation + " is not supported " + message);
    }
}
