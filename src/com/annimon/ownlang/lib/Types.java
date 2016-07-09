package com.annimon.ownlang.lib;

public final class Types {

    public static final int
            OBJECT = 0,
            NUMBER = 1,
            STRING = 2,
            ARRAY = 3,
            MAP = 4,
            FUNCTION = 5;
    
    private static final int FIRST = OBJECT, LAST = FUNCTION;
    private static final String[] NAMES = {"object", "number", "string", "array", "map", "function"};
    
    public static String typeToString(int type) {
        if (FIRST <= type && type <= LAST) {
            return NAMES[type];
        }
        return "unknown (" + type + ")";
    }
}
