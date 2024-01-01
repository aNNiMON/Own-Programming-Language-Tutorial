package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;

final class functional_dropWhile implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function predicate = ValueUtils.consumeFunction(args[1], 1);
        return dropWhile(container, predicate);
    }
    
    static ArrayValue dropWhile(Value container, Function predicate) {
        if (container.type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        return dropWhileArray((ArrayValue) container, predicate);
    }

    static ArrayValue dropWhileArray(ArrayValue array, Function predicate) {
        int skipCount = 0;
        for (Value value : array) {
            if (predicate.execute(value) != NumberValue.ZERO)
                skipCount++;
            else break;
        }

        final int size = array.size();
        final Value[] result = new Value[size - skipCount];
        System.arraycopy(array.getCopyElements(), skipCount, result, 0, result.length);
        return new ArrayValue(result);
    }
}