package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

public final class UnknownClassException extends OwnLangRuntimeException {
    
    private final String className;

    public UnknownClassException(String name, Range range) {
        super("Unknown class " + name, range);
        this.className = name;
    }

    public String getClassName() {
        return className;
    }
}
