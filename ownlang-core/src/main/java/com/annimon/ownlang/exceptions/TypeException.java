package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

public final class TypeException extends OwnLangRuntimeException {

    public TypeException(String message) {
        super(message);
    }

    public TypeException(String message, Range range) {
        super(message, range);
    }
}
