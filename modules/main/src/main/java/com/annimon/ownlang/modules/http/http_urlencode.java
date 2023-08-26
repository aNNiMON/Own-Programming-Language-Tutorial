package com.annimon.ownlang.modules.http;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import java.io.IOException;
import java.net.URLEncoder;

public final class http_urlencode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.checkOrOr(1, 2, args.length);
        
        String charset = "UTF-8";
        if (args.length >= 2) {
            charset = args[1].asString();
        }
        
        try {
            final String result = URLEncoder.encode(args[0].asString(), charset);
            return new StringValue(result);
        } catch (IOException ex) {
            return args[0];
        }
    }
}