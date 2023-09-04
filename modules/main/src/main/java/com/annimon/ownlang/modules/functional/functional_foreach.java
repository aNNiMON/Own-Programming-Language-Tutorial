package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Map;

public final class functional_foreach implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function consumer = ValueUtils.consumeFunction(args[1], 1);
        final int argsCount = consumer.getArgsCount();

        switch (container.type()) {
            case Types.STRING:
                final StringValue string = (StringValue) container;
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

            case Types.ARRAY:
                final ArrayValue array = (ArrayValue) container;
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

            case Types.MAP:
                final MapValue map = (MapValue) container;
                for (Map.Entry<Value, Value> element : map) {
                    consumer.execute(element.getKey(), element.getValue());
                }
                return map;

            default:
                throw new TypeException("Cannot iterate " + Types.typeToString(container.type()));
        }
    }
}
