package com.annimon.ownlang.modules.functional;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class functional implements Module {

    @Override
    public Map<String, Value> constants() {
        return Map.of("IDENTITY", new FunctionValue(args -> args[0]));
    }

    @Override
    public Map<String, Function> functions() {
        final var result = new HashMap<String, Function>(15);
        result.put("foreach", new functional_forEach());
        result.put("map", new functional_map());
        result.put("flatmap", new functional_flatmap());
        result.put("reduce", new functional_reduce());
        result.put("filter", new functional_filter());
        result.put("sortby", new functional_sortBy());
        result.put("takewhile", new functional_takeWhile());
        result.put("dropwhile", new functional_dropWhile());

        result.put("chain", new functional_chain());
        result.put("stream", new functional_stream());
        result.put("combine", new functional_combine());
        return result;
    }
}
