package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Converters;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.StringValue;
import java.util.List;
import java.util.Map;
import okhttp3.Response;

public class ResponseValue extends MapValue {

    private final Response response;

    public ResponseValue(Response response) {
        super(15);
        this.response = response;
        init();
    }

    private void init() {
        set("body", args -> new ResponseBodyValue(response.body()));
        set("cacheResponse", args -> new ResponseValue(response.cacheResponse()));
        set("code", Converters.voidToInt(response::code));
        set("close", Converters.voidToVoid(response::close));
        set("header", args -> {
            Arguments.checkOrOr(1, 2, args.length);
            final String defaultValue = (args.length == 1) ? null : args[1].asString();
            return new StringValue(response.header(args[0].asString(), defaultValue));
        });
        set("headers", args -> {
            Arguments.checkOrOr(0, 1, args.length);
            if (args.length == 0) {
                final Map<String, List<String>> headers = response.headers().toMultimap();
                final MapValue result = new MapValue(headers.size());
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    result.set(entry.getKey(), ArrayValue.of(entry.getValue().toArray(new String[0])));
                }
                return result;
            } else {
                return ArrayValue.of(response.headers(args[0].asString()).toArray(new String[0]));
            }
        });
        set("message", Converters.voidToString(response::message));
        set("networkResponse", args -> new ResponseValue(response.networkResponse()));
        set("priorResponse", args -> new ResponseValue(response.priorResponse()));
        set("receivedResponseAtMillis", Converters.voidToLong(response::receivedResponseAtMillis));
        set("sentRequestAtMillis", Converters.voidToLong(response::sentRequestAtMillis));
    }

}
