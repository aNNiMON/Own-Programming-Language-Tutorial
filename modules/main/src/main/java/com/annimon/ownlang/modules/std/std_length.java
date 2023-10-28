package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.*;

final class std_length implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(1, args.length);

        final Value value = args[0];
        final int length = switch (value.type()) {
            case Types.ARRAY -> ((ArrayValue) value).size();
            case Types.MAP -> ((MapValue) value).size();
            case Types.CLASS -> ((ClassInstance) value).getThisMap().size();
            case Types.STRING -> ((StringValue) value).length();
            case Types.FUNCTION -> {
                final Function func = ((FunctionValue) value).getValue();
                yield func.getArgsCount();
            }
            default -> 0;
        };
        return NumberValue.of(length);
    }
}