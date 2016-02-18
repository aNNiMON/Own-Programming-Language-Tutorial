package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_newarray implements Function {

    @Override
    public Value execute(Value... args) {
        return createArray(args, 0);
    }

    private ArrayValue createArray(Value[] args, int index) {
        final int size = args[index].asInt();
        final int last = args.length - 1;
        ArrayValue array = new ArrayValue(size);
        if (index == last) {
            for (int i = 0; i < size; i++) {
                array.set(i, NumberValue.ZERO);
            }
        } else if (index < last) {
            for (int i = 0; i < size; i++) {
                array.set(i, createArray(args, index + 1));
            }
        }
        return array;
    }
}