package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_tolowercase implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 1) throw new ArgumentsMismatchException("One argument expected");
        
        return new StringValue(args[0].asString().toLowerCase());
    }
}