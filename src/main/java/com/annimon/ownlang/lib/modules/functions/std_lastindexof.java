package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;

public final class std_lastindexof implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(2, 3, args.length);
        
        final String input = args[0].asString();
        final String what = args[1].asString();
        if (args.length == 2) {
            return NumberValue.of(input.lastIndexOf(what));
        }
        final int index = args[2].asInt();
        return NumberValue.of(input.lastIndexOf(what, index));
    }
}