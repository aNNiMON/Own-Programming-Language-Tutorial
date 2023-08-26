package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

public final class std_substring implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(2, 3, args.length);
        
        final String input = args[0].asString();
        final int startIndex = args[1].asInt();
        
        String result;
        if (args.length == 2) {
            result = input.substring(startIndex);
        } else {
            final int endIndex = args[2].asInt();
            result = input.substring(startIndex, endIndex);
        }
        
        return new StringValue(result);
    }
}
