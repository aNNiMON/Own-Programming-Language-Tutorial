package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

public final class ArgumentsMismatchException extends OwnLangRuntimeException {

    public ArgumentsMismatchException() {
    }

    public ArgumentsMismatchException(String message) {
        super(message);
    }

    public ArgumentsMismatchException(String message, Range range) {
        super(message, range);
    }
}
