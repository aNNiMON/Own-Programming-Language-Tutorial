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

public final class functional_foreach implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function consumer = ValueUtils.consumeFunction(args[1], 1);
        if (container.type() == Types.ARRAY) {
            final ArrayValue array = (ArrayValue) container;
            for (Value element : array) {
                consumer.execute(element);
            }
            return array;
        }
        if (container.type() == Types.MAP) {
            final MapValue map = (MapValue) container;
            for (Map.Entry<Value, Value> element : map) {
                consumer.execute(element.getKey(), element.getValue());
            }
            return map;
        }
        throw new TypeException("Invalid first argument. Array or map expected");
    }
}
