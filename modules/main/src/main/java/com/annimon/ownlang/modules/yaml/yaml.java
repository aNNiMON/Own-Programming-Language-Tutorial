package com.annimon.ownlang.modules.yaml;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class yaml implements Module {

    @Override
    public Map<String, Value> constants() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.of(
                "yamlencode", new yaml_encode(),
                "yamldecode", new yaml_decode()
        );
    }
}
