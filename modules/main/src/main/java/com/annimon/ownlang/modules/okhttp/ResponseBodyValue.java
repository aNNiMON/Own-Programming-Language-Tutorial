package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Converters;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import java.io.IOException;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;

public class ResponseBodyValue extends MapValue {

    private final ResponseBody responseBody;

    public ResponseBodyValue(ResponseBody response) {
        super(8);
        this.responseBody = response;
        init();
    }

    private void init() {
        set("bytes", args -> {
            try {
                return ArrayValue.of(responseBody.bytes());
            } catch (IOException e) {
                throw new OwnLangRuntimeException(e);
            }
        });
        set("close", Converters.voidToVoid(responseBody::close));
        set("contentLength", Converters.voidToLong(responseBody::contentLength));
        set("contentType", args -> new StringValue(responseBody.contentType().toString()));
        set("string", args -> {
            try {
                return new StringValue(responseBody.string());
            } catch (IOException e) {
                throw new OwnLangRuntimeException(e);
            }
        });
        set("file", args -> {
            Arguments.check(1, args.length);
            try {
                BufferedSink sink = Okio.buffer(Okio.sink(Console.fileInstance(args[0].asString())));
                sink.writeAll(responseBody.source());
                sink.close();
                return NumberValue.ONE;
            } catch (IOException e) {
                throw new OwnLangRuntimeException(e);
            }
        });
    }

}
