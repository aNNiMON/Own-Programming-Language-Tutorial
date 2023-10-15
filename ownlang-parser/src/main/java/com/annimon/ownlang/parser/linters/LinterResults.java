package com.annimon.ownlang.parser.linters;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

class LinterResults extends AbstractList<LinterResult> {
    private final List<LinterResult> results;

    LinterResults() {
        this(new ArrayList<>());
    }

    LinterResults(List<LinterResult> results) {
        this.results = results;
    }

    @Override
    public boolean add(LinterResult result) {
        return results.add(result);
    }

    @Override
    public LinterResult get(int index) {
        return results.get(index);
    }

    @Override
    public Iterator<LinterResult> iterator() {
        return results.iterator();
    }

    @Override
    public int size() {
        return results.size();
    }

    public boolean hasErrors() {
        return results.stream().anyMatch(LinterResult::isError);
    }

    public Stream<LinterResult> errors() {
        return results.stream().filter(LinterResult::isError);
    }

    public boolean hasWarnings() {
        return results.stream().anyMatch(LinterResult::isWarning);
    }

    public Stream<LinterResult> warnings() {
        return results.stream().filter(LinterResult::isWarning);
    }
}
