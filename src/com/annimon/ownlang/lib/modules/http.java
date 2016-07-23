package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.annotations.ConstantInitializer;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

/**
 *
 * @author aNNiMON
 */
@ConstantInitializer
public final class http implements Module {

    public static void initConstants() {
    }

    @Override
    public void init() {
        initConstants();
        Functions.set("urlencode", new http_urlencode());
        Functions.set("http", new http_http());
        Functions.set("download", new http_download());
    }
}
