package com.annimon.ownlang.lib;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

final class RootScope extends Scope {
    private final Map<String, Value> constants;
    private final Map<String, Function> functions;
    private final Map<String, ClassDeclaration> classDeclarations;
    private final Set<String> loadedModules;

    RootScope() {
        functions = new ConcurrentHashMap<>();
        constants = new ConcurrentHashMap<>();
        classDeclarations = new ConcurrentHashMap<>();
        constants.put("true", NumberValue.ONE);
        constants.put("false", NumberValue.ZERO);
        loadedModules = new CopyOnWriteArraySet<>();
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


    public ClassDeclaration getClassDeclaration(String name) {
        return classDeclarations.get(name);
    }

    public void setClassDeclaration(ClassDeclaration classDeclaration) {
        classDeclarations.put(classDeclaration.name(), classDeclaration);
    }

    public Map<String, ClassDeclaration> getClassDeclarations() {
        return classDeclarations;
    }

    public Set<String> getLoadedModules() {
        return loadedModules;
    }

    public boolean isModuleLoaded(String name) {
        return loadedModules.contains(name);
    }

    public void addLoadedModule(String name) {
        loadedModules.add(name);
    }
}
