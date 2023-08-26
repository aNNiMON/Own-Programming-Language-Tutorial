package com.annimon.ownlang.modules.std;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

public final class std_sleep implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        
        try {
            Thread.sleep((long) args[0].asNumber());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return NumberValue.ZERO;
    }
}