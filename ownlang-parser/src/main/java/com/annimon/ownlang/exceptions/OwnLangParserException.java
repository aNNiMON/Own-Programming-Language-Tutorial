package com.annimon.ownlang.exceptions;

import com.annimon.ownlang.util.SourceLocatedError;
import java.util.Collection;
import java.util.List;

/**
 * Single Exception for Lexer, Parser and Linter errors
 */
public class OwnLangParserException extends RuntimeException {

    private final Collection<? extends SourceLocatedError> errors;

    public OwnLangParserException(SourceLocatedError error) {
        super(error.toString());
        errors = List.of(error);;
    }

    public OwnLangParserException(Collection<? extends SourceLocatedError> errors) {
        super(errors.toString());
        this.errors = errors;
    }

    public Collection<? extends SourceLocatedError> getParseErrors() {
        return errors;
    }
}