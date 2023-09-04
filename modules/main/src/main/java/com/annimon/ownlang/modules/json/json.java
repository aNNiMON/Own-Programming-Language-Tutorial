package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
public final class json implements Module {

    public static void initConstants() {
    }

    @Override
    public void init() {
        initConstants();
        ScopeHandler.setFunction("jsonencode", new json_encode());
        ScopeHandler.setFunction("jsondecode", new json_decode());
    }
}
