package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

/**
 *
 * @author aNNiMON
 */
public final class functional implements Module {

    @Override
    public void init() {
        Functions.set("foreach", new functional_foreach());
        Functions.set("map", new functional_map());
        Functions.set("flatmap", new functional_flatmap());
        Functions.set("reduce", new functional_reduce());
        Functions.set("filter", new functional_filter());
        
        Functions.set("combine", new functional_combine());
        
        Variables.set("IDENTITY", new FunctionValue(args -> args[0]));
    }
}
