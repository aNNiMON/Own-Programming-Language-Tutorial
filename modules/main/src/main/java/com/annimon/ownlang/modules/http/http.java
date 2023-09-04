package com.annimon.ownlang.modules.http;

import com.annimon.ownlang.lib.ScopeHandler;
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
        ScopeHandler.setFunction("urlencode", new http_urlencode());
        ScopeHandler.setFunction("http", new http_http());
        ScopeHandler.setFunction("download", new http_download());
    }
}
