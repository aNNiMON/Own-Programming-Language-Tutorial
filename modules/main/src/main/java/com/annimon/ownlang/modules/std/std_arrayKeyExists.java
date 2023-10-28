package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

final class std_arrayKeyExists implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        if (args[1].type() != Types.MAP) {
            throw new TypeException("Map expected in second argument");
        }
        final MapValue map = ((MapValue) args[1]);
        return NumberValue.fromBoolean(map.containsKey(args[0]));
    }
    
}
