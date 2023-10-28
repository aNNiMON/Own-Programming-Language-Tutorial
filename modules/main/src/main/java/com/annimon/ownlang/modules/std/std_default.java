package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.*;

final class std_default implements Function {

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
        return switch (value.type()) {
            case Types.NUMBER -> (value.asInt() == 0);
            case Types.STRING -> (value.asString().isEmpty());
            case Types.ARRAY -> ((ArrayValue) value).size() == 0;
            case Types.MAP -> ((MapValue) value).size() == 0;
            case Types.CLASS -> ((ClassInstance) value).getThisMap().size() == 0;
            default -> false;
        };
    }
}