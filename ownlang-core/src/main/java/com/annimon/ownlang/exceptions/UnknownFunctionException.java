package com.annimon.ownlang.exceptions;

public final class UnknownFunctionException extends OwnLangRuntimeException {
    
    private final String functionName;

    public UnknownFunctionException(String name) {
        super("Unknown function " + name);
        this.functionName = name;
    }

    public String getFunctionName() {
        return functionName;
    }
}
