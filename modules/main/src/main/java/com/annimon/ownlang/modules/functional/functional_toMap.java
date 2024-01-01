package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.LinkedHashMap;
import java.util.Map;

final class functional_toMap implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkRange(2, 4, args.length);
        final Value container = args[0];
        final Function keyMapper = ValueUtils.consumeFunction(args[1], 1);
        final Function valueMapper = args.length >= 3
                ? ValueUtils.consumeFunction(args[2], 2)
                : null;
        final Function merger = args.length >= 4
                ? ValueUtils.consumeFunction(args[3], 3)
                : null;
        return toMap(container, keyMapper, valueMapper, merger);
    }

    static MapValue toMap(Value container, Function keyMapper, Function valueMapper, Function merger) {
        return switch (container.type()) {
            case Types.ARRAY -> toMap((ArrayValue) container, keyMapper, valueMapper, merger);
            case Types.MAP -> toMap((MapValue) container, keyMapper, valueMapper, merger);
            default -> throw new TypeException("Cannot iterate " + Types.typeToString(container.type()));
        };
    }

    static MapValue toMap(ArrayValue array, Function keyMapper, Function valueMapper, Function merger) {
        final Map<Value, Value> result = new LinkedHashMap<>(array.size());
        for (Value element : array) {
            final Value key = keyMapper.execute(element);
            final Value value = valueMapper != null
                    ? valueMapper.execute(element)
                    : element;
            final Value oldValue = result.get(key);
            final Value newValue = (oldValue == null || merger == null)
                    ? value
                    : merger.execute(oldValue, value);
            result.put(key, newValue);
        }
        return new MapValue(result);
    }

    static MapValue toMap(MapValue map, Function keyMapper, Function valueMapper, Function merger) {
        final Map<Value, Value> result = new LinkedHashMap<>(map.size());
        for (Map.Entry<Value, Value> element : map) {
            final Value key = keyMapper.execute(element.getKey(), element.getValue());
            final Value value = valueMapper != null
                    ? valueMapper.execute(element.getKey(), element.getValue())
                    : element.getValue();
            final Value oldValue = result.get(key);
            final Value newValue = (oldValue == null || merger == null)
                    ? value
                    : merger.execute(oldValue, value);
            result.put(key, newValue);
        }
        return new MapValue(result);
    }
}
