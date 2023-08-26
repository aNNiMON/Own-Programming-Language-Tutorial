package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_echo implements Function {

    @Override
    public Value execute(Value... args) {
        final StringBuilder sb = new StringBuilder();
        for (Value arg : args) {
            sb.append(arg.asString());
            sb.append(" ");
        }
        Console.println(sb.toString());
        return NumberValue.ZERO;
    }
}
