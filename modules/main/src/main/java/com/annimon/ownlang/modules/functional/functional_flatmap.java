package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.util.ArrayList;
import java.util.List;

public final class functional_flatmap implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        final Function mapper = ValueUtils.consumeFunction(args[1], 1);
        return flatMapArray((ArrayValue) args[0], mapper);
    }
    
    private Value flatMapArray(ArrayValue array, Function mapper) {
        final List<Value> values = new ArrayList<>();
        final int size = array.size();
        for (int i = 0; i < size; i++) {
            final Value inner = mapper.execute(array.get(i));
            if (inner.type() != Types.ARRAY) {
                throw new TypeException("Array expected " + inner);
            }
            for (Value value : (ArrayValue) inner) {
                values.add(value);
            }
        }
        return new ArrayValue(values);
    }
}
