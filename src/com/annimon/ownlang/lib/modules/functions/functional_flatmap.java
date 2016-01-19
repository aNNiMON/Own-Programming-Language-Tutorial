package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;

public final class functional_flatmap implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length < 2) throw new ArgumentsMismatchException("At least two arguments expected");
        
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        if (args[1].type() != Types.FUNCTION) {
            throw new TypeException("Function expected in second argument");
        }
        
        final Function mapper = ((FunctionValue) args[1]).getValue();
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