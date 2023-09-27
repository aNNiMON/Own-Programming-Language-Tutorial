package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.parser.error.ParseError;
import com.annimon.ownlang.parser.error.ParseErrors;

/**
 * Single Exception for Lexer and Parser errors
 */
public class OwnLangParserException extends RuntimeException {

    private final ParseErrors parseErrors;

    public OwnLangParserException(ParseError parseError) {
        super(parseError.toString());
        this.parseErrors = new ParseErrors();
        parseErrors.add(parseError);;
    }

    public OwnLangParserException(ParseErrors parseErrors) {
        super(parseErrors.toString());
        this.parseErrors = parseErrors;
    }

    public ParseErrors getParseErrors() {
        return parseErrors;
    }
}