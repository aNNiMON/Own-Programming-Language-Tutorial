package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

import java.util.Random;

public final class std_rand implements Function {

    private static final Random RND = new Random();

    @Override
    public Value execute(Value... args) {
        if (args.length == 0) return new NumberValue(RND.nextDouble());

        int from = 0;
        int to = 100;
        if (args.length == 1) {
            to = (int) args[0].asNumber();
        } else if (args.length == 2) {
            from = (int) args[0].asNumber();
            to = (int) args[1].asNumber();
        }
        return new NumberValue(RND.nextInt(to - from) + from);
    }
}