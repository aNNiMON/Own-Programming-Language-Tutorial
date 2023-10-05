package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_stream implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);

        if (args.length > 1) {
            return new StreamValue(new ArrayValue(args));
        }

        final Value value = args[0];
        switch (value.type()) {
            case Types.MAP:
                return new StreamValue(((MapValue) value).toPairs());
            case Types.ARRAY:
                return new StreamValue((ArrayValue) value);
            default:
                throw new TypeException("Invalid argument. Array or map expected");
        }
    }
}
