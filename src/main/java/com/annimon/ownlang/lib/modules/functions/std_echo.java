package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_echo implements Function {

    @Override
    public Value execute(Value... args) {
        for (Value arg : args) {
            Console.print(arg.asString());
            Console.print(" ");
        }
        Console.println();
        return NumberValue.ZERO;
    }
}