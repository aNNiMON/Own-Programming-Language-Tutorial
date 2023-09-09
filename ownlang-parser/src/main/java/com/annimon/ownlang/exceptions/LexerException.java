package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.Pos;

/**
 *
 * @author aNNiMON
 */
public final class LexerException extends RuntimeException {

    public LexerException(String message) {
        super(message);
    }

    public LexerException(Pos pos, String message) {
        super(pos.format() + " " + message);
    }
}