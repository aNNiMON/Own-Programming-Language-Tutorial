package com.annimon.ownlang.util;

public interface SourceLocation {

    default Range getRange() {
        return null;
    }
}
