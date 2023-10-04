package com.annimon.ownlang.util;

public interface SourceLocation {

    default Range getRange() {
        return null;
    }

    default String formatRange() {
        final var range = getRange();
        return range == null ? "" : range.format();
    }
}
