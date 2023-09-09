package com.annimon.ownlang.modules.http;

import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class http implements Module {

    @Override
    public Map<String, Value> constants() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.of(
                "urlencode", new http_urlencode(),
                "http", new http_http(),
                "download", new http_download()
        );
    }
}
