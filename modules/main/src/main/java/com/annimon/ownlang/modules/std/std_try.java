package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.*;

public final class std_try implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        try {
            return ValueUtils.consumeFunction(args[0], 0).execute();
        } catch (Exception ex) {
            if (args.length == 2) {
                switch (args[1].type()) {
                    case Types.FUNCTION:
                        final String message = ex.getMessage();
                        final Function catchFunction = ((FunctionValue) args[1]).getValue();
                        return catchFunction.execute(
                                new StringValue(ex.getClass().getName()),
                                new StringValue(message == null ? "" : message));
                    default:
                        return args[1];
                }
            }
            return NumberValue.MINUS_ONE;
        }
    }
    
}
