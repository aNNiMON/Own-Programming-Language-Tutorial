package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

public final class UnknownFunctionException extends OwnLangRuntimeException {
    
    private final String functionName;

    public UnknownFunctionException(String name) {
        super("Unknown function " + name);
        this.functionName = name;
    }

    public UnknownFunctionException(String name, Range range) {
        super("Unknown function " + name, range);
        this.functionName = name;
    }

    public String getFunctionName() {
        return functionName;
    }
}
