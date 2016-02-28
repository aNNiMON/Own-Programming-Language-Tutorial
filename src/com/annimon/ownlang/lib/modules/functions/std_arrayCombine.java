package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class std_arrayCombine implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 2) throw new ArgumentsMismatchException("Two arguments expected");
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        if (args[1].type() != Types.ARRAY) {
            throw new TypeException("Array expected in second argument");
        }
        
        final ArrayValue keys = ((ArrayValue) args[0]);
        final ArrayValue values = ((ArrayValue) args[1]);
        final int length = Math.min(keys.size(), values.size());
        
        final MapValue result = new MapValue(length);
        for (int i = 0; i < length; i++) {
            result.set(keys.get(i), values.get(i));
        }
        return result;
    }
    
}