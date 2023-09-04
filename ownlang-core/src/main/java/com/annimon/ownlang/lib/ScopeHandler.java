package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.UnknownFunctionException;
import com.annimon.ownlang.lib.Scope.ScopeFindData;

import java.util.Map;

public final class ScopeHandler {

    private static final Object lock = new Object();

    private static volatile RootScope rootScope;
    private static volatile Scope scope;

    static {
        ScopeHandler.resetScope();
    }

    public static Map<String, Value> variables() {
        return scope.getVariables();
    }

    public static Map<String, Value> constants() {
        return rootScope.getConstants();
    }

    public static Map<String, Function> functions() {
        return rootScope.getFunctions();
    }

    /**
     * Resets a scope for new program execution
     */
    public static void resetScope() {
        rootScope = new RootScope();
        scope = rootScope;
    }

    public static void push() {
        synchronized (lock) {
            scope = new Scope(scope);
        }
    }

    public static void pop() {
        synchronized (lock) {
            if (!scope.isRoot()) {
                scope = scope.parent;
            }
        }
    }



    public static boolean isFunctionExists(String name) {
        return rootScope.containsFunction(name);
    }

    public static Function getFunction(String name) {
        final var function = rootScope.getFunction(name);
        if (function == null) throw new UnknownFunctionException(name);
        return function;
    }

    public static void setFunction(String name, Function function) {
        rootScope.setFunction(name, function);
    }


    public static boolean isVariableOrConstantExists(String name) {
        synchronized (lock) {
            return findScope(name).isFound;
        }
    }

    public static Value getVariableOrConstant(String name) {
        synchronized (lock) {
            final ScopeFindData scopeData = findScope(name);
            if (scopeData.isFound) {
                return scopeData.scope.get(name);
            }
        }
        return NumberValue.ZERO;
    }

    public static Value getVariable(String name) {
        synchronized (lock) {
            final ScopeFindData scopeData = findScope(name);
            if (scopeData.isFound) {
                return scopeData.scope.getVariable(name);
            }
        }
        // TODO should be strict
        return NumberValue.ZERO;
    }

    public static void setVariable(String name, Value value) {
        synchronized (lock) {
            findScope(name).scope.setVariable(name, value);
        }
    }

    public static boolean isConstantExists(String name) {
        return rootScope.containsConstant(name);
    }

    public static void setConstant(String name, Value value) {
        rootScope.setConstant(name, value);
    }

    public static void defineVariableInCurrentScope(String name, Value value) {
        synchronized (lock) {
            scope.setVariable(name, value);
        }
    }

    public static void removeVariable(String name) {
        synchronized (lock) {
            findScope(name).scope.removeVariable(name);
        }
    }

    private static ScopeFindData findScope(String name) {
        Scope current = scope;
        do {
            if (current.contains(name)) {
                return new ScopeFindData(true, current);
            }
        } while ((current = current.parent) != null);

        return new ScopeFindData(false, scope);
    }
}
