package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.lib.*;

public final class functional_filterNot implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.check(2, args.length);
        final Value container = args[0];
        final Function predicate = ValueUtils.consumeFunction(args[1], 1);
        return functional_filter.filter(container, negate(predicate));
    }

    static Function negate(Function f) {
        return args -> NumberValue.fromBoolean(f.execute(args) == NumberValue.ZERO);
    }
}
