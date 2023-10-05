package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

public final class functional_stream implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkAtLeast(1, args.length);

        final Value value = args[0];
        return switch (value.type()) {
            case Types.MAP -> new StreamValue(((MapValue) value).toPairs());
            case Types.ARRAY -> new StreamValue((ArrayValue) value);
            default -> throw new TypeException("Invalid argument. Array or map expected");
        };
    }
}
