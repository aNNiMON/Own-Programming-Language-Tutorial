package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;

public final class std_charat implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
        final String input = args[0].asString();
        final int index = args[1].asInt();
        
        return new NumberValue((short)input.charAt(index));
    }
}