package com.annimon.ownlang.lib;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class CallStack {
    
    private static final Deque<CallInfo> calls = new ConcurrentLinkedDeque<>();

    private CallStack() { }
    
    public static synchronized void clear() {
        calls.clear();
    }
    
    public static synchronized void enter(String name, Function function) {
        calls.push(new CallInfo(name, function.toString()));
    }
    
    public static synchronized void exit() {
        calls.pop();
    }

    public static synchronized Deque<CallInfo> getCalls() {
        return calls;
    }
    
    public record CallInfo(String name, String function) {
        @Override
        public String toString() {
            return String.format("%s: %s", name, function);
        }
    }
}
