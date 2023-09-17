package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.Pos;
import com.annimon.ownlang.parser.Range;

/**
 *
 * @author aNNiMON
 */
public final class ParseException extends RuntimeException {

    private final Range range;

    public ParseException(String message) {
        this(message, Range.ZERO);
    }

    public ParseException(String message, Pos pos) {
        this(message, pos, pos);
    }

    public ParseException(String message, Pos start, Pos end) {
        this(message, new Range(start, end));
    }

    public ParseException(String message, Range range) {
        super(message);
        this.range = range;
    }

    public Range getRange() {
        return range;
    }
}