package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.modules.functions.json_decode;
import com.annimon.ownlang.lib.modules.functions.json_encode;

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
