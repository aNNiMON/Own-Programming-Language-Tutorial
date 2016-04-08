package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_chain implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkAtLeast(2, args.length);

        Value result = args[0];
        for (int i = 1; i < args.length; i += 2) {
            final Value arg = args[i];
            if (arg.type() != Types.FUNCTION) {
                throw new TypeException(arg.toString() + " is not a function");
            }
            final Function function = ((FunctionValue) arg).getValue();
            result = function.execute(result, args[i+1]);
        }
        return result;
    }
    
}