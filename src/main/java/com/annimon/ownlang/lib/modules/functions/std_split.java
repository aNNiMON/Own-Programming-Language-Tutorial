package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

public final class std_split implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(2, 3, args.length);

        final String input = args[0].asString();
        final String regex = args[1].asString();
        final int limit = (args.length == 3) ? args[2].asInt() : 0;

        final String[] parts = input.split(regex, limit);
        return ArrayValue.of(parts);
    }
}
