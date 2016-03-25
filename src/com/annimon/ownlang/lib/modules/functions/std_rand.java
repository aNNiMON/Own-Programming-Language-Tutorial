package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

import java.util.Random;

public final class std_rand implements Function {

    private static final Random RND = new Random();

    @Override
    public Value execute(Value... args) {
        Arguments.checkRange(0, 2, args.length);
        if (args.length == 0) return new NumberValue(RND.nextDouble());

        int from = 0;
        int to = 100;
        if (args.length == 1) {
            to = args[0].asInt();
        } else if (args.length == 2) {
            from = args[0].asInt();
            to = args[1].asInt();
        }
        return new NumberValue(RND.nextInt(to - from) + from);
    }
}