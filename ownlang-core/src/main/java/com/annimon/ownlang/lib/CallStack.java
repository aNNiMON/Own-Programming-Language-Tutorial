package com.annimon.ownlang.lib;

import com.annimon.ownlang.util.Range;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public final class CallStack {

    private static final int MAX_FUNCTION_LENGTH = 62;
    private static final Deque<CallInfo> calls = new ConcurrentLinkedDeque<>();

    private CallStack() { }
    
    public static synchronized void clear() {
        calls.clear();
    }
    
    public static synchronized void enter(String name, Function function, Range range) {
        String func = function.toString();
        if (func.contains("com.annimon.ownlang.modules")) {
            func = func.replaceAll(
                    "com.annimon.ownlang.modules.(\\w+)\\.?\\1?", "module $1");
        }
        if (func.contains("\n")) {
            func = func.substring(0, func.indexOf("\n")).trim();
        }
        if (func.length() > MAX_FUNCTION_LENGTH) {
            func = func.substring(0, MAX_FUNCTION_LENGTH) + "...";
        }
        calls.push(new CallInfo(name, func, range));
    }
    
    public static synchronized void exit() {
        calls.pop();
    }

    public static synchronized Deque<CallInfo> getCalls() {
        return calls;
    }

    public static String getFormattedCalls() {
        return calls.stream()
                .map(CallInfo::format)
                .collect(Collectors.joining("\n"));
    }
    
    public record CallInfo(String name, String function, Range range) {
        String format() {
            return "\tat " + this;
        }

        @Override
        public String toString() {
            if (range == null) {
                return String.format("%s: %s", name, function);
            } else {
                return String.format("%s: %s %s", name, function, range.format());
            }
        }
    }
}
