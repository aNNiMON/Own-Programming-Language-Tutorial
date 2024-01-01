package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class functional_filter implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function predicate = ValueUtils.consumeFunction(args[1], 1);
        return filter(container, predicate);
    }

    static Value filter(Value container, Function predicate) {
        if (container.type() == Types.ARRAY) {
            return filterArray((ArrayValue) container, predicate);
        }
        if (container.type() == Types.MAP) {
            return filterMap((MapValue) container, predicate);
        }
        throw new TypeException("Invalid first argument. Array or map expected");
    }

    static ArrayValue filterArray(ArrayValue array, Function predicate) {
        final int size = array.size();
        final List<Value> values = new ArrayList<>(size);
        for (Value value : array) {
            if (predicate.execute(value) != NumberValue.ZERO) {
                values.add(value);
            }
        }
        return new ArrayValue(values);
    }

    static MapValue filterMap(MapValue map, Function predicate) {
        final MapValue result = new MapValue(map.size());
        for (Map.Entry<Value, Value> element : map) {
            if (predicate.execute(element.getKey(), element.getValue()) != NumberValue.ZERO) {
                result.set(element.getKey(), element.getValue());
            }
        }
        return result;
    }
}
