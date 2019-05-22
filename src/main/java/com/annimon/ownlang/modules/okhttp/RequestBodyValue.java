package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Converters;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.StringValue;
import java.io.IOException;
import java.nio.charset.Charset;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestBodyValue extends MapValue {

    private final RequestBody requestBody;
    private final MediaType mediaType;
    
    public RequestBodyValue(RequestBody requestBody) {
        this(requestBody, 0);
    }

    protected RequestBodyValue(RequestBody requestBody, int methodsCount) {
        super(4 + methodsCount);
        this.requestBody = requestBody;
        this.mediaType = requestBody.contentType();
        init();
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    private void init() {
        set("getContentLength", Converters.voidToLong(() -> {
            try {
                return requestBody.contentLength();
            } catch (IOException ex) {
                return -1;
            }
        }));
        set("getType", Converters.voidToString(mediaType::type));
        set("getSubtype", Converters.voidToString(mediaType::subtype));
        set("getCharset", args -> {
            Arguments.checkOrOr(0, 1, args.length);
            if (args.length == 0) {
                return new StringValue(mediaType.charset().name());
            } else {
                return new StringValue(mediaType.charset(Charset.forName(args[0].asString())).name());
            }
        });
    }
}
