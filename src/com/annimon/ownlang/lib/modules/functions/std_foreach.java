package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

import java.util.Map;

public final class std_foreach implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 2) throw new ArgumentsMismatchException("Two arguments expected");

        if (args[1].type() != Types.FUNCTION) throw new TypeException("Second arg must be a function");
        final Function function = ((FunctionValue) args[1]).getValue();
        final Value container = args[0];
        if (container.type() == Types.ARRAY) {
            final ArrayValue array = (ArrayValue) container;
            for (Value element : array) {
                function.execute(element);
            }
            return NumberValue.ZERO;
        }
        if (container.type() == Types.MAP) {
            final MapValue map = (MapValue) container;
            for (Map.Entry<Value, Value> element : map) {
                function.execute(element.getKey(), element.getValue());
            }
            return NumberValue.ZERO;
        }
        throw new TypeException("First arg must be an array or map");
    }
}