package com.annimon.ownlang.exceptions;

/**
 *
 * @author aNNiMON
 */
public final class ParseException extends RuntimeException {
    
    public ParseException() {
        super();
    }
    
    public ParseException(String string) {
        super(string);
    }
}