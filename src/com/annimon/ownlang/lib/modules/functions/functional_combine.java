package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_combine implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkAtLeast(1, args.length);
        Function result = null;
        for (Value arg : args) {
            if (arg.type() != Types.FUNCTION) {
                throw new TypeException(arg.toString() + " is not a function");
            }
            final Function current = result;
            final Function next = ((FunctionValue) arg).getValue();
            result = fArgs -> {
                if (current == null) return next.execute(fArgs);
                return next.execute(current.execute(fArgs));
            };
        }
        
        return new FunctionValue(result);
    }
    
}