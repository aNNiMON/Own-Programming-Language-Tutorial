package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_forEachIndexed implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function consumer = ValueUtils.consumeFunction(args[1], 1);
        return forEachIndexed(container, consumer);
    }

    static Value forEachIndexed(Value container, Function consumer) {
        if (container.type() == Types.ARRAY) {
            return forEachIndexedArray((ArrayValue) container, consumer);
        }
        // Only used in Streams -> no Map implementation
        throw new TypeException("Cannot iterate " + Types.typeToString(container.type()));
    }

    static ArrayValue forEachIndexedArray(ArrayValue array, Function consumer) {
        int index = 0;
        for (Value element : array) {
            consumer.execute(element, NumberValue.of(index++));
        }
        return array;
    }
}
