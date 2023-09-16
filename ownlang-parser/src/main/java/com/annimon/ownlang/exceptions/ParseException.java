package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.Pos;

/**
 *
 * @author aNNiMON
 */
public final class ParseException extends RuntimeException {

    private final Pos start;
    private final Pos end;

    public ParseException(String message) {
        this(message, Pos.ZERO, Pos.ZERO);
    }

    public ParseException(String message, Pos pos) {
        this(message, pos, pos);
    }

    public ParseException(String message, Pos start, Pos end) {
        super(message);
        this.start = start;
        this.end = end;
    }

    public Pos getStart() {
        return start;
    }

    public Pos getEnd() {
        return end;
    }
}