package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.UnknownClassException;
import java.util.HashMap;
import java.util.Map;

public final class Classes {

    private static final Map<String, ClassInstanceValue> classes;
    static {
        classes = new HashMap<>();
    }

    private Classes() { }

    public static void clear() {
        classes.clear();
    }

    public static Map<String, ClassInstanceValue> getFunctions() {
        return classes;
    }
    
    public static boolean isExists(String key) {
        return classes.containsKey(key);
    }
    
    public static ClassInstanceValue get(String key) {
        if (!isExists(key)) throw new UnknownClassException(key);
        return classes.get(key);
    }
    
    public static void set(String key, ClassInstanceValue classDef) {
        classes.put(key, classDef);
    }
}
