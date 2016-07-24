package com.annimon.ownlang.parser.optimization;

import com.annimon.ownlang.lib.Value;

public final class VariableInfo {
    public Value value;
    public int modifications;

    @Override
    public String toString() {
        return (value == null ? "?" : value) + " (" + modifications + " mods)";
    }
}