package com.annimon.ownlang.parser;

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