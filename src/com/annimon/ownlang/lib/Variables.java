package com.annimon.ownlang.lib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Variables {

    private static final Map<String, Double> variables;
    
    static {
        variables = new HashMap<>();
        variables.put("PI", Math.PI);
        variables.put("ПИ", Math.PI);
        variables.put("E", Math.E);
        variables.put("GOLDEN_RATIO", 1.618);
    }
    
    public static boolean isExists(String key) {
        return variables.containsKey(key);
    }
    
    public static double get(String key) {
        if (!isExists(key)) return 0;
        return variables.get(key);
    }
    
    public static void set(String key, double value) {
        variables.put(key, value);
    }
}
