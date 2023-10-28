package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

final class std_join implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkRange(1, 4, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        
        final ArrayValue array = (ArrayValue) args[0];
        return switch (args.length) {
            case 1 -> ArrayValue.joinToString(array, "", "", "");
            case 2 -> ArrayValue.joinToString(array, args[1].asString(), "", "");
            case 3 -> ArrayValue.joinToString(array, args[1].asString(), args[2].asString(), args[2].asString());
            case 4 -> ArrayValue.joinToString(array, args[1].asString(), args[2].asString(), args[3].asString());
            default -> throw new ArgumentsMismatchException("Wrong number of arguments");
        };
    }
}
