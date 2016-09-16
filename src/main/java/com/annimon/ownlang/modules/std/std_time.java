package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_time implements Function {

    @Override
    public Value execute(Value... args) {
        return NumberValue.of(System.currentTimeMillis());
    }
}