package com.annimon.ownlang.lib;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class CallStack {
    
    private static final Deque<CallInfo> calls = new ConcurrentLinkedDeque<>();

    private CallStack() { }
    
    public static synchronized void clear() {
        calls.clear();
    }
    
    public static synchronized void enter(String name, Function function, String position) {
        String func = function.toString();
        if (func.contains("com.annimon.ownlang.modules")) {
            func = func.replaceAll(
                    "com.annimon.ownlang.modules.(\\w+)\\.?\\1?", "module $1");
        }
        if (func.contains("\n")) {
            func = func.substring(0, func.indexOf("\n")).trim();
        }
        calls.push(new CallInfo(name, func, position));
    }
    
    public static synchronized void exit() {
        calls.pop();
    }

    public static synchronized Deque<CallInfo> getCalls() {
        return calls;
    }
    
    public record CallInfo(String name, String function, String position) {
        @Override
        public String toString() {
            if (position == null) {
                return String.format("%s: %s", name, function);
            } else {
                return String.format("%s: %s %s", name, function, position);
            }
        }
    }
}
