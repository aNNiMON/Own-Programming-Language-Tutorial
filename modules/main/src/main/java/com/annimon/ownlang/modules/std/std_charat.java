package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

final class std_charat implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);

        final String input = args[0].asString();
        final int index = args[1].asInt();
        
        return NumberValue.of((short)input.charAt(index));
    }
}
