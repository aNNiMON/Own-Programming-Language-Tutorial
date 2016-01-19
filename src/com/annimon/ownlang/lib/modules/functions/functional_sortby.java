package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.Arrays;

public final class functional_sortby implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 2) throw new ArgumentsMismatchException("Two arguments expected");
        
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        if (args[1].type() != Types.FUNCTION) {
            throw new TypeException("Function expected in second argument");
        }
        
        final Value[] elements = ((ArrayValue) args[0]).getCopyElements();
        final Function function = ((FunctionValue) args[1]).getValue();
        Arrays.sort(elements, (o1, o2) -> function.execute(o1).compareTo(function.execute(o2)));
        return new ArrayValue(elements);
    }
    
}