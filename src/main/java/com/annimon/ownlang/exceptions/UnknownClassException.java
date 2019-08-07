package com.annimon.ownlang.exceptions;

public final class UnknownClassException extends RuntimeException {
    
    private final String className;

    public UnknownClassException(String name) {
        super("Unknown class " + name);
        this.className = name;
    }

    public String getClassName() {
        return className;
    }
}
