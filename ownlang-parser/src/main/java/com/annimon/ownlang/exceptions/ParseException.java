package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.Range;

/**
 *
 * @author aNNiMON
 */
public final class ParseException extends BaseParserException {

    public ParseException(String message) {
        super(message, Range.ZERO);
    }

    public ParseException(String message, Range range) {
        super(message, range);
    }
}