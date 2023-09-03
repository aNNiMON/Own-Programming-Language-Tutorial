package com.annimon.ownlang.lib;

import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Variables {
    private Variables() { }

    public static Map<String, Value> variables() {
        return ScopeHandler.variables();
    }
    
    public static boolean isExists(String name) {
        return ScopeHandler.isVariableOrConstantExists(name);
    }
    
    public static Value get(String name) {
        return ScopeHandler.getVariableOrConstant(name);
    }
    
    public static void set(String name, Value value) {
        ScopeHandler.setVariable(name, value);
    }

    /**
     * For compatibility with other modules
     */
    public static void define(String name, Value value) {
        ScopeHandler.setConstant(name, value);
    }
}
