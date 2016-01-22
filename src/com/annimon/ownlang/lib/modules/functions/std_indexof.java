package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_indexof implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length < 2 || args.length > 3) throw new ArgumentsMismatchException("Two or three arguments expected");
        
        final String input = args[0].asString();
        final String what = args[1].asString();
        final int index = (args.length == 3) ? ((int) args[2].asNumber()) : 0;
        
        return new NumberValue(input.indexOf(what, index));
    }
}