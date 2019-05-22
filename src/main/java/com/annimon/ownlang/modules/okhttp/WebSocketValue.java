package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Converters;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.ValueUtils;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketValue extends MapValue {

    private final WebSocket ws;

    protected WebSocketValue(WebSocket ws) {
        super(4);
        this.ws = ws;
        init();
    }

    public WebSocket getWebSocket() {
        return ws;
    }

    private void init() {
        set("cancel", Converters.voidToVoid(ws::cancel));
        set("close", args -> {
            Arguments.checkOrOr(1, 2, args.length);
            final String reason = (args.length == 2) ? args[1].asString() : null;
            return NumberValue.fromBoolean(ws.close(args[0].asInt(), reason));
        });
        set("queueSize", Converters.voidToLong(ws::queueSize));
        set("send", args -> {
            Arguments.check(1, args.length);
            final boolean result;
            if (args[0].type() == Types.ARRAY) {
                result = ws.send(ByteString.of( ValueUtils.toByteArray(((ArrayValue) args[0])) ));
            } else {
                result = ws.send(args[0].asString());
            }
            return NumberValue.fromBoolean(result);
        });
    }
}
