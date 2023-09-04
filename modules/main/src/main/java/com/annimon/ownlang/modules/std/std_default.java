package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

public final class std_default implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        if (isEmpty(args[0])) {
            return args[1];
        }
        return args[0];
    }

    private boolean isEmpty(Value value) {
        if (value == null || value.raw() == null) {
            return true;
        }
        switch (value.type()) {
            case Types.NUMBER:
                return (value.asInt() == 0);
            case Types.STRING:
                return (value.asString().isEmpty());
            case Types.ARRAY:
                return ((ArrayValue) value).size() == 0;
            case Types.MAP:
                return ((MapValue) value).size() == 0;
            default:
                return false;
        }
    }
}