package com.annimon.ownlang.parser.error;

import com.annimon.ownlang.Console;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ParseErrors extends AbstractList<ParseError> {

    private final List<ParseError> errors;

    public ParseErrors() {
        errors = new ArrayList<>();
    }
    
    public void clear() {
        errors.clear();
    }

    @Override
    public boolean add(ParseError parseError) {
        return errors.add(parseError);
    }

    @Override
    public ParseError get(int index) {
        return errors.get(index);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public Iterator<ParseError> iterator() {
        return errors.iterator();
    }

    @Override
    public int size() {
        return errors.size();
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        for (ParseError error : errors) {
            result.append(error).append(Console.newline());
        }
        return result.toString();
    }
}
