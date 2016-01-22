package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_replaceall implements Function {
    
    @Override
    public Value execute(Value... args) {
        if (args.length != 3) throw new ArgumentsMismatchException("Three arguments expected");
        
        final String input = args[0].asString();
        final String regex = args[1].asString();
        final String replacement = args[2].asString();
        
        return new StringValue(input.replaceAll(regex, replacement));
    }
}