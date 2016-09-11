package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

public final class std_arrayCombine implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
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
