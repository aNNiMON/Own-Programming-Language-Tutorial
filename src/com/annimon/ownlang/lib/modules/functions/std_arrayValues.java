package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class std_arrayValues implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 1) throw new ArgumentsMismatchException("One argument expected");
        if (args[0].type() != Types.MAP) {
            throw new TypeException("Map expected in first argument");
        }
        final MapValue map = ((MapValue) args[0]);
        final List<Value> values = new ArrayList<>(map.size());
        for (Map.Entry<Value, Value> entry : map) {
            values.add(entry.getValue());
        }
        return new ArrayValue(values);
    }
    
}