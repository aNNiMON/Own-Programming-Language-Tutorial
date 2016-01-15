package com.annimon.ownlang.lib;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author aNNiMON
 */
public final class Variables {

    private static final Stack<Map<String, Value>> stack;
    private static Map<String, Value> variables;
    
    static {
        stack = new Stack<>();
        variables = new ConcurrentHashMap<>();
        variables.put("true", NumberValue.ONE);
        variables.put("false", NumberValue.ZERO);
    }
    
    public static void push() {
        stack.push(new ConcurrentHashMap<>(variables));
    }
    
    public static void pop() {
        variables = stack.pop();
    }
    
    public static boolean isExists(String key) {
        return variables.containsKey(key);
    }
    
    public static Value get(String key) {
        if (!isExists(key)) return NumberValue.ZERO;
        return variables.get(key);
    }
    
    public static void set(String key, Value value) {
        variables.put(key, value);
    }
    
    public static void remove(String key) {
        variables.remove(key);
    }
}
