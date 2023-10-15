package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.Range;

/**
 *
 * @author aNNiMON
 */
public final class ParseException extends RuntimeException {

    private final Range range;

    public ParseException(String message, Range range) {
        super(message);
        this.range = range;
    }

    public Range getRange() {
        return range;
    }
}