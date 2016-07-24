package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.exceptions.ArgumentsMismatchException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class std_join implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkRange(1, 4, args.length);
        if (args[0].type() != Types.ARRAY) {
            throw new TypeException("Array expected in first argument");
        }
        
        final ArrayValue array = (ArrayValue) args[0];
        switch (args.length) {
            case 1:
                return join(array, "", "", "");
            case 2:
                return join(array, args[1].asString(), "", "");
            case 3:
                return join(array, args[1].asString(), args[2].asString(), args[2].asString());
            case 4:
                return join(array, args[1].asString(), args[2].asString(), args[3].asString());
            default:
                throw new ArgumentsMismatchException("Wrong number of arguments");
        }
    }
    
    private static StringValue join(ArrayValue array, String delimiter, String prefix, String suffix) {
        final StringBuilder sb = new StringBuilder();
        for (Value value : array) {
            if (sb.length() > 0) sb.append(delimiter);
            else sb.append(prefix);
            sb.append(value.asString());
        }
        sb.append(suffix);
        return new StringValue(sb.toString());
    }
}