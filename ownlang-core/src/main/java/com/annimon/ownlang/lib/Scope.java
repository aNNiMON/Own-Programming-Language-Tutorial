package com.annimon.ownlang.lib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

sealed class Scope permits RootScope {
    final Scope parent;
    private final Map<String, Value> variables;

    Scope() {
        this(null);
    }

    Scope(Scope parent) {
        this.parent = parent;
        variables = new ConcurrentHashMap<>();
    }

    public boolean isRoot() {
        return !hasParent();
    }

    public boolean hasParent() {
        return parent != null;
    }

    public boolean contains(String name) {
        return containsVariable(name);
    }

    public final boolean containsVariable(String name) {
        return variables.containsKey(name);
    }

    public Value get(String name) {
        return getVariable(name);
    }

    public final Value getVariable(String name) {
        return variables.get(name);
    }

    public final void setVariable(String name, Value value) {
        variables.put(name, value);
    }

    public Map<String, Value> getVariables() {
        return variables;
    }

    static class ScopeFindData {
        final boolean isFound;
        final Scope scope;

        ScopeFindData(boolean isFound, Scope scope) {
            this.isFound = isFound;
            this.scope = scope;
        }
    }
}