package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.lib.Functions;
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
        Functions.set("jsonencode", new json_encode());
        Functions.set("jsondecode", new json_decode());
    }
}
