package com.annimon.ownlang.lib;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Constants {

    private static final Map<String, Double> constants;
    
    static {
        constants = new HashMap<>();
        constants.put("PI", Math.PI);
        constants.put("ПИ", Math.PI);
        constants.put("E", Math.E);
        constants.put("GOLDEN_RATIO", 1.618);
    }
    
    public static boolean isExists(String key) {
        return constants.containsKey(key);
    }
    
    public static double get(String key) {
        if (!isExists(key)) return 0;
        return constants.get(key);
    }
}
