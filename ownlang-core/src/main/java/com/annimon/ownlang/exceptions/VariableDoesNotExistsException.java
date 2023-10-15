package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

public final class VariableDoesNotExistsException extends OwnLangRuntimeException {
    
    private final String variable;

    public VariableDoesNotExistsException(String variable, Range range) {
        super("Variable " + variable + " does not exists", range);
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }
}
