package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_echo implements Function {

    @Override
    public Value execute(Value... args) {
        for (Value arg : args) {
            System.out.print(arg.asString());
            System.out.print(" ");
        }
        System.out.println();
        return NumberValue.ZERO;
    }
}