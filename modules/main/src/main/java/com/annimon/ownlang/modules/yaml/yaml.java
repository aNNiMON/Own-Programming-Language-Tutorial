package com.annimon.ownlang.modules.yaml;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
public final class yaml implements Module {

    @Override
    public void init() {
        ScopeHandler.setFunction("yamlencode", new yaml_encode());
        ScopeHandler.setFunction("yamldecode", new yaml_decode());
    }
}
