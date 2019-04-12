package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;
import java.io.UnsupportedEncodingException;

public final class StringFunctions {

    private StringFunctions() { }

    static ArrayValue getBytes(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final String charset = (args.length == 2) ? args[1].asString() : "UTF-8";
        try {
            return ArrayValue.of(args[0].asString().getBytes(charset));
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }
    
    static Value parseInt(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Integer.parseInt(args[0].asString(), radix));
    }

    static Value parseLong(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final int radix = (args.length == 2) ? args[1].asInt() : 10;
        return NumberValue.of(Long.parseLong(args[0].asString(), radix));
    }
}
