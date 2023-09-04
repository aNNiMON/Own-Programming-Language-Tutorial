package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
public final class functional implements Module {

    public static void initConstants() {
        ScopeHandler.setConstant("IDENTITY", new FunctionValue(args -> args[0]));
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setFunction("foreach", new functional_foreach());
        ScopeHandler.setFunction("map", new functional_map());
        ScopeHandler.setFunction("flatmap", new functional_flatmap());
        ScopeHandler.setFunction("reduce", new functional_reduce());
        ScopeHandler.setFunction("filter", new functional_filter(false));
        ScopeHandler.setFunction("sortby", new functional_sortby());
        ScopeHandler.setFunction("takewhile", new functional_filter(true));
        ScopeHandler.setFunction("dropwhile", new functional_dropwhile());

        ScopeHandler.setFunction("chain", new functional_chain());
        ScopeHandler.setFunction("stream", new functional_stream());
        ScopeHandler.setFunction("combine", new functional_combine());
    }
}
