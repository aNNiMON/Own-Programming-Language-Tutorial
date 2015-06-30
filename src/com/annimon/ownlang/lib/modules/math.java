package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class math implements Module {

    @Override
    public void init() {
        Functions.set("sin", args -> {
            if (args.length != 1) throw new RuntimeException("One arg expected");
            return new NumberValue(Math.sin(args[0].asNumber()));
        });
        Functions.set("cos", args -> {
            if (args.length != 1) throw new RuntimeException("One arg expected");
            return new NumberValue(Math.cos(args[0].asNumber()));
        });
    }

}
