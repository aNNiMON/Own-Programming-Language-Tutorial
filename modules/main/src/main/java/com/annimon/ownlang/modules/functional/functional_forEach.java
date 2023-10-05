package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Map;

public final class functional_forEach implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function consumer = ValueUtils.consumeFunction(args[1], 1);
        return forEach(container, consumer);
    }

    static Value forEach(Value container, Function consumer) {
        final int argsCount = consumer.getArgsCount();
        return switch (container.type()) {
            case Types.STRING -> forEachString((StringValue) container, argsCount, consumer);
            case Types.ARRAY -> forEachArray((ArrayValue) container, argsCount, consumer);
            case Types.MAP -> forEachMap((MapValue) container, consumer);
            default -> throw new TypeException("Cannot iterate " + Types.typeToString(container.type()));
        };
    }

    static StringValue forEachString(StringValue string, int argsCount, Function consumer) {
        if (argsCount == 2) {
            for (char ch : string.asString().toCharArray()) {
                consumer.execute(new StringValue(String.valueOf(ch)), NumberValue.of(ch));
            }
        } else {
            for (char ch : string.asString().toCharArray()) {
                consumer.execute(new StringValue(String.valueOf(ch)));
            }
        }
        return string;
    }

    static ArrayValue forEachArray(ArrayValue array, int argsCount, Function consumer) {
        if (argsCount == 2) {
            int index = 0;
            for (Value element : array) {
                consumer.execute(element, NumberValue.of(index++));
            }
        } else {
            for (Value element : array) {
                consumer.execute(element);
            }
        }
        return array;
    }

    static MapValue forEachMap(MapValue map, Function consumer) {
        for (Map.Entry<Value, Value> element : map) {
            consumer.execute(element.getKey(), element.getValue());
        }
        return map;
    }
}
