package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.util.Map;

public final class functional_reduce implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(3, args.length);
        
        final Value container = args[0];
        final Value identity = args[1];
        final Function accumulator = ValueUtils.consumeFunction(args[2], 2);
        if (container.type() == Types.ARRAY) {
            Value result = identity;
            final ArrayValue array = (ArrayValue) container;
            for (Value element : array) {
                result = accumulator.execute(result, element);
            }
            return result;
        }
        if (container.type() == Types.MAP) {
            Value result = identity;
            final MapValue map = (MapValue) container;
            for (Map.Entry<Value, Value> element : map) {
                result = accumulator.execute(result, element.getKey(), element.getValue());
            }
            return result;
        }
        throw new TypeException("Invalid first argument. Array or map expected");
    }
}
