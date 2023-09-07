package com.annimon.ownlang.lib;

public final class AutoCloseableScope implements AutoCloseable {
    @Override
    public void close() {
        ScopeHandler.pop();
    }
}
