package com.annimon.ownlang.lib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Functions {

    private static final Map<String, Function> functions;
    
    static {
        functions = new HashMap<>();
    }
    
    public static boolean isExists(String key) {
        return functions.containsKey(key);
    }
    
    public static Function get(String key) {
        if (!isExists(key)) throw new RuntimeException("Unknown function " + key);
        return functions.get(key);
    }
    
    public static void set(String key, Function function) {
        functions.put(key, function);
    }
}
