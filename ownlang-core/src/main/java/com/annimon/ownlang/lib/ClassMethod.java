package com.annimon.ownlang.lib;

import java.util.Objects;

public class ClassMethod implements Function {
    private final String name;
    private final Function function;
    private ClassInstance classInstance;

    public ClassMethod(String name, Function function) {
        this.name = name;
        this.function = function;
    }

    public String getName() {
        return name;
    }

    public void setClassInstance(ClassInstance classInstance) {
        this.classInstance = classInstance;
    }

    @Override
    public Value execute(Value... args) {
        ScopeHandler.push();
        ScopeHandler.defineVariableInCurrentScope("this", classInstance.getThisMap());

        try {
            return function.execute(args);
        } finally {
            ScopeHandler.pop();
        }
    }

    @Override
    public int getArgsCount() {
        return function.getArgsCount();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ClassMethod) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.function, that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, function);
    }

    @Override
    public String toString() {
        return "ClassMethod[" +
                "name=" + name + ", " +
                "function=" + function + ']';
    }
}
