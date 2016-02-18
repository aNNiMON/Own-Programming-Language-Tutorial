package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_substring implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length < 2 || args.length > 3) throw new ArgumentsMismatchException("Two or three arguments expected");
        
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