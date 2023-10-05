package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.util.Arrays;
import java.util.Comparator;

public final class functional_sortBy implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected at first argument");
        }

        final Value[] elements = ((ArrayValue) args[0]).getCopyElements();
        final Function function = ValueUtils.consumeFunction(args[1], 1);
        Arrays.sort(elements, Comparator.comparing(function::execute));
        return new ArrayValue(elements);
    }
    
}
