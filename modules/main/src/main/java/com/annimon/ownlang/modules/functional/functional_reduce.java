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

final class functional_reduce implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(3, args.length);
        
        final Value container = args[0];
        final Value identity = args[1];
        final Function accumulator = ValueUtils.consumeFunction(args[2], 2);
        return reduce(container, identity, accumulator);
    }

    static Value reduce(Value container, Value identity, Function accumulator) {
        if (container.type() == Types.ARRAY) {
            return reduceArray(identity, (ArrayValue) container, accumulator);
        }
        if (container.type() == Types.MAP) {
            return reduceMap(identity, (MapValue) container, accumulator);
        }
        throw new TypeException("Invalid first argument. Array or map expected");
    }

    static Value reduceArray(Value identity, ArrayValue array, Function accumulator) {
        Value result = identity;
        for (Value element : array) {
            result = accumulator.execute(result, element);
        }
        return result;
    }

    static Value reduceMap(Value identity, MapValue map, Function accumulator) {
        Value result = identity;
        for (Map.Entry<Value, Value> element : map) {
            result = accumulator.execute(result, element.getKey(), element.getValue());
        }
        return result;
    }
}
