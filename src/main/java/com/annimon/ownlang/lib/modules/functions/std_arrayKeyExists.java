package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class std_arrayKeyExists implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(2, args.length);
        if (args[1].type() != Types.MAP) {
            throw new TypeException("Map expected in second argument");
        }
        final MapValue map = ((MapValue) args[1]);
        return NumberValue.fromBoolean(map.containsKey(args[0]));
    }
    
}