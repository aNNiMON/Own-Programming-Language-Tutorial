package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.*;

final class SystemFunctions {

    private SystemFunctions() { }

    static Value exit(Value[] args) {
        Arguments.check(1, args.length);
        System.exit(args[0].asInt());
        return NumberValue.ZERO;
    }

    static Value getenv(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final var env = System.getenv(args[0].asString());
        if (env == null) {
            return args.length == 2 ? args[1] : StringValue.EMPTY;
        }
        return new StringValue(env);
    }

    static Value getprop(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final var env = System.getProperty(args[0].asString());
        if (env == null) {
            return args.length == 2 ? args[1] : StringValue.EMPTY;
        }
        return new StringValue(env);
    }

    static Value time(Value[] args) {
        Arguments.check(0, args.length);
        return NumberValue.of(System.currentTimeMillis());
    }

    static Value nanotime(Value[] args) {
        Arguments.check(0, args.length);
        return NumberValue.of(System.nanoTime());
    }
}
