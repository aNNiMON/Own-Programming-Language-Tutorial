package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.Pos;

/**
 *
 * @author aNNiMON
 */
public final class LexerException extends OwnLangParserException {

    public LexerException(String message) {
        super(message);
    }

    public LexerException(String message, Pos pos) {
        super(pos.format() + " " + message);
    }
}