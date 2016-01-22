package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_tochar implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 1) throw new ArgumentsMismatchException("One argument expected");
        
        return new StringValue(String.valueOf((char) args[0].asNumber()));
    }
}