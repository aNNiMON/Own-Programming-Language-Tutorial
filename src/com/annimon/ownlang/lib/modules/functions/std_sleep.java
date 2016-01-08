package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_sleep implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length == 1) {
            try {
                Thread.sleep((long) args[0].asNumber());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return NumberValue.ZERO;
    }
}