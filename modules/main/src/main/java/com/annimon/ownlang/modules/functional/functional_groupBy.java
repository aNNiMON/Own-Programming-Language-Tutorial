package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class functional_groupBy implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        
        final Value container = args[0];
        final Function classifier = ValueUtils.consumeFunction(args[1], 1);
        return groupBy(container, classifier);
    }

    static Value groupBy(Value container, Function classifier) {
        if (container.type() == Types.ARRAY) {
            return groupByArray((ArrayValue) container, classifier);
        }
        if (container.type() == Types.MAP) {
            return groupByMap((MapValue) container, classifier);
        }
        throw new TypeException("Invalid first argument. Array or map expected");
    }

    @SuppressWarnings("Java8MapApi")
    static Value groupByArray(ArrayValue array, Function classifier) {
        final var result = new LinkedHashMap<Value, List<Value>>();
        for (Value element : array) {
            final var key = classifier.execute(element);
            var container = result.get(key);
            if (container == null) {
                container = new ArrayList<>();
                result.put(key, container);
            }
            container.add(element);
        }
        return fromMapOfArrays(result);
    }

    static Value groupByMap(MapValue map, Function classifier) {
        final var result = new LinkedHashMap<Value, Value>();
        for (Map.Entry<Value, Value> element : map) {
            final var k = element.getKey();
            final var v = element.getValue();
            final var key = classifier.execute(k, v);
            var container = (MapValue) result.get(key);
            if (container == null) {
                container = new MapValue(10);
                result.put(key, container);
            }
            container.set(k, v);
        }
        return new MapValue(result);
    }

    private static MapValue fromMapOfArrays(Map<Value, List<Value>> map) {
        final var result = new LinkedHashMap<Value, Value>();
        map.forEach((key, value) -> result.put(key, new ArrayValue(value)));
        return new MapValue(result);
    }
}
