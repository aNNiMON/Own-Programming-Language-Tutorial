package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public final class Variables {
    private Variables() { }

    /**
     * @deprecated This function remains for backward compatibility with old separate modules
     * Use {@link ScopeHandler#setVariable(String, Value)}
     */
    @Deprecated
    public static void set(String name, Value value) {
        ScopeHandler.setVariable(name, value);
    }

    /**
     * @deprecated This function remains for backward compatibility with old separate modules
     * Use {@link ScopeHandler#setConstant(String, Value)}
     */
    @Deprecated
    public static void define(String name, Value value) {
        ScopeHandler.setConstant(name, value);
    }
}
