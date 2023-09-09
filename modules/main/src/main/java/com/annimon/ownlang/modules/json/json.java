package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class json implements Module {

    @Override
    public Map<String, Value> constants() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.of(
                "jsonencode", new json_encode(),
                "jsondecode", new json_decode()
        );
    }
}
