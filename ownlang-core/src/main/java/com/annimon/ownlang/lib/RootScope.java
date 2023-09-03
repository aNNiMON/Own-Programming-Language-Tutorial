package com.annimon.ownlang.lib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class RootScope extends Scope {
    private final Map<String, Value> constants;
    private final Map<String, Function> functions;

    RootScope() {
        functions = new ConcurrentHashMap<>();
        constants = new ConcurrentHashMap<>();
        constants.put("true", NumberValue.ONE);
        constants.put("false", NumberValue.ZERO);
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public boolean contains(String name) {
        return super.containsVariable(name)
                || containsConstant(name);
    }

    public boolean containsConstant(String name) {
        return constants.containsKey(name);
    }

    @Override
    public Value get(String name) {
        if (containsConstant(name)) {
            return getConstant(name);
        }
        return super.get(name);
    }

    public Value getConstant(String name) {
        return constants.get(name);
    }

    public void setConstant(String name, Value value) {
        constants.put(name, value);
    }

    public Map<String, Value> getConstants() {
        return constants;
    }


    public boolean containsFunction(String name) {
        return functions.containsKey(name);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public void setFunction(String name, Function function) {
        functions.put(name, function);
    }

    public Map<String, Function> getFunctions() {
        return functions;
    }
}
