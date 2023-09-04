package com.annimon.ownlang.lib;

import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class Functions {
    private Functions() { }

    public static Map<String, Function> getFunctions() {
        return ScopeHandler.functions();
    }

    /**
     * @deprecated This function remains for backward compatibility with old separate modules
     * Use {@link ScopeHandler#setFunction(String, Function)}
     */
    @Deprecated
    public static void set(String key, Function function) {
        ScopeHandler.setFunction(key, function);
    }
}
