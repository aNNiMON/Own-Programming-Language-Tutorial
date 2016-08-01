package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

/**
 *
 * @author aNNiMON
 */
@ConstantInitializer
public final class functional implements Module {

    public static void initConstants() {
        Variables.define("IDENTITY", new FunctionValue(args -> args[0]));
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("foreach", new functional_foreach());
        Functions.set("map", new functional_map());
        Functions.set("flatmap", new functional_flatmap());
        Functions.set("reduce", new functional_reduce());
        Functions.set("filter", new functional_filter(false));
        Functions.set("sortby", new functional_sortby());
        Functions.set("takewhile", new functional_filter(true));
        Functions.set("dropwhile", new functional_dropwhile());

        Functions.set("chain", new functional_chain());
        Functions.set("combine", new functional_combine());
    }
}
