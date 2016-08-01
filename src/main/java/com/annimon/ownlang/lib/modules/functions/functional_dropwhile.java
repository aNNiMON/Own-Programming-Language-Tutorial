package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_dropwhile implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        if (args[1].type() != Types.FUNCTION) {
            throw new TypeException("Function expected in second argument");
        }

        final Value container = args[0];
        final Function predicate = ((FunctionValue) args[1]).getValue();
        return dropWhileArray((ArrayValue) container, predicate);
    }
    
    private Value dropWhileArray(ArrayValue array, Function predicate) {
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