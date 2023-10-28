package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.util.Arrays;

final class std_sort implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        final Value[] elements = ((ArrayValue) args[0]).getCopyElements();

        switch (args.length) {
            case 1 -> Arrays.sort(elements);
            case 2 -> {
                final Function comparator = ValueUtils.consumeFunction(args[1], 1);
                Arrays.sort(elements, (o1, o2) -> comparator.execute(o1, o2).asInt());
            }
            default -> throw new ArgumentsMismatchException("Wrong number of arguments");
        }
        
        return new ArrayValue(elements);
    }
    
}
