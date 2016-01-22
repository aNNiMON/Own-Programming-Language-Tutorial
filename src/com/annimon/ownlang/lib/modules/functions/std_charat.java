package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_charat implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 2) throw new ArgumentsMismatchException("Two arguments expected");
        
        final String input = args[0].asString();
        final int index = (int) args[1].asNumber();
        
        return new NumberValue(input.charAt(index));
    }
}