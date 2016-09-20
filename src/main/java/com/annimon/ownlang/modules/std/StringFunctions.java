package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class StringFunctions {

    public static Value parseInt(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Integer.parseInt(args[0].asString(), radix));
    }

    public static Value parseLong(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Long.parseLong(args[0].asString(), radix));
    }
}
