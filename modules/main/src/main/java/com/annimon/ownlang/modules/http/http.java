package com.annimon.ownlang.modules.http;

import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.modules.Module;

/**
 *
 * @author aNNiMON
 */
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
