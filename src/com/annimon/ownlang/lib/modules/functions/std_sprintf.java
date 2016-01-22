package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.lib.*;

public final class std_sprintf implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length < 1) throw new ArgumentsMismatchException("At least one argument expected");
        
        final String format = args[0].asString();
        final Object[] values = new Object[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            values[i - 1] = (args[i].type() == Types.NUMBER) ? args[i].asNumber() : args[i].asString();
        }
        return new StringValue(String.format(format, values));
    }
}