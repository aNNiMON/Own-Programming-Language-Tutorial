package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;

public final class std_tochar implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        return new StringValue(String.valueOf((char) args[0].asInt()));
    }
}