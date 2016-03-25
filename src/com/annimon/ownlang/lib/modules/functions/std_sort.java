package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Arrays;

public final class std_sort implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkAtLeast(1, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        final Value[] elements = ((ArrayValue) args[0]).getCopyElements();
        
        switch (args.length) {
            case 1:
                Arrays.sort(elements);
                break;
            case 2:
                if (args[1].type() != Types.FUNCTION) {
                    throw new TypeException("Function expected in second argument");
                }
                final Function comparator = ((FunctionValue) args[1]).getValue();
                Arrays.sort(elements, (o1, o2) -> comparator.execute(o1, o2).asInt());
                break;
            default:
                throw new ArgumentsMismatchException("Wrong number of arguments");
        }
        
        return new ArrayValue(elements);
    }
    
}