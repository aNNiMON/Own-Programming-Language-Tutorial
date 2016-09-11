package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

public final class std_replace implements Function {
    
    @Override
    public Value execute(Value... args) {
        Arguments.check(3, args.length);
        
        final String input = args[0].asString();
        final String target = args[1].asString();
        final String replacement = args[2].asString();
        
        return new StringValue(input.replace(target, replacement));
    }
}
