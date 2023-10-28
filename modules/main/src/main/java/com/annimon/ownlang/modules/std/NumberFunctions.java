package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

final class NumberFunctions {

    private NumberFunctions() { }

    static Value toHexString(Value[] args) {
        Arguments.check(1, args.length);
        long value;
        if (args[0].type() == Types.NUMBER) {
            value = ((NumberValue) args[0]).asLong();
        } else {
            value = (long) args[0].asNumber();
        }
        return new StringValue(Long.toHexString(value));
    }
}
