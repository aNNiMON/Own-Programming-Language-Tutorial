package com.annimon.ownlang.lib;

import com.annimon.ownlang.modules.Module;

public final class ModuleLoader {
    private static final String PACKAGE = "com.annimon.ownlang.modules.%s.%s";

    private ModuleLoader() { }

    public static Module load(String name) {
        try {
            return (Module) Class.forName(String.format(PACKAGE, name, name))
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load module " + name, ex);
        }
    }

    public static void loadAndUse(String name) {
        final var rootScope = ScopeHandler.rootScope();
        if (rootScope.isModuleLoaded(name)) return;

        final var module = load(name);
        rootScope.getConstants().putAll(module.constants());
        rootScope.getFunctions().putAll(module.functions());
    }
}
