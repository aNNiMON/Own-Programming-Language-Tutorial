package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class std_try implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        if (args[0].type() != Types.FUNCTION) {
            throw new TypeException(args[0].toString() + " is not a function");
        }
        try {
            return ((FunctionValue) args[0]).getValue().execute();
        } catch (Exception ex) {
            if (args.length == 2 && args[1].type() == Types.FUNCTION) {
                final String message = ex.getMessage();
                final Function catchFunction = ((FunctionValue) args[1]).getValue();
                return catchFunction.execute(
                        new StringValue(ex.getClass().getName()),
                        new StringValue(message == null ? "" : message));
            }
            return NumberValue.MINUS_ONE;
        }
    }
    
}