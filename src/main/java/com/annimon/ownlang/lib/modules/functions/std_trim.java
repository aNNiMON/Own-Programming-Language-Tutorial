package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

public final class std_trim implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        return new StringValue(args[0].asString().trim());
    }
}
