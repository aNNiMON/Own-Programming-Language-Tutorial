package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

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
