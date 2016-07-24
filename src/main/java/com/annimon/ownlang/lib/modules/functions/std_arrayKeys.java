package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class std_arrayKeys implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        if (args[0].type() != Types.MAP) {
            throw new TypeException("Map expected in first argument");
        }
        final MapValue map = ((MapValue) args[0]);
        final List<Value> keys = new ArrayList<>(map.size());
        for (Map.Entry<Value, Value> entry : map) {
            keys.add(entry.getKey());
        }
        return new ArrayValue(keys);
    }
    
}