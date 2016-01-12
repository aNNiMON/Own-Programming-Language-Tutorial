package com.annimon.ownlang.lib.modules;

import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.lib.modules.functions.*;

/**
 *
 * @author aNNiMON
 */
public final class http implements Module {

    @Override
    public void init() {
        Functions.set("urlencode", new http_urlencode());
        Functions.set("http", new http_http());
    }
}
