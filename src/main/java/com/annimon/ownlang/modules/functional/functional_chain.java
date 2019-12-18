package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;

public final class functional_chain implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkAtLeast(2, args.length);

        Value result = args[0];
        for (int i = 1; i < args.length; i += 2) {
            final Function function = ValueUtils.consumeFunction(args[i], i);
            result = function.execute(result, args[i+1]);
        }
        return result;
    }

}
