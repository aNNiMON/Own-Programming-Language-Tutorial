package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;

import java.util.Map;

public final class std_foreach implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 2) return NumberValue.ZERO;

        if (!(args[1] instanceof FunctionValue)) return NumberValue.ZERO;
        final Function function = ((FunctionValue) args[1]).getValue();
        final Value container = args[0];
        if (container instanceof ArrayValue) {
            final ArrayValue array = (ArrayValue) container;
            for (Value element : array) {
                function.execute(element);
            }
            return NumberValue.ZERO;
        }
        if (container instanceof MapValue) {
            final MapValue map = (MapValue) container;
            for (Map.Entry<Value, Value> element : map) {
                function.execute(element.getKey(), element.getValue());
            }
            return NumberValue.ZERO;
        }
        return NumberValue.ZERO;
    }
}