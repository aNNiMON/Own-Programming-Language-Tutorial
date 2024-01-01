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

final class functional_flatmap implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function mapper = ValueUtils.consumeFunction(args[1], 1);
        return flatMap(container, mapper);
    }

    static Value flatMap(Value container, Function mapper) {
        if (container.type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        return flatMapArray((ArrayValue) container, mapper);
    }

    static Value flatMapArray(ArrayValue array, Function mapper) {
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
