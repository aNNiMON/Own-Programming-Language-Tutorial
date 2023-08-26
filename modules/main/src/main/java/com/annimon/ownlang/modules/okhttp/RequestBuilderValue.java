package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Converters.VoidToVoidFunction;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestBuilderValue extends MapValue {
    
    private final Request.Builder requestBuilder;
    
    public RequestBuilderValue() {
        super(15);
        requestBuilder = new Request.Builder();
        init();
    }

    public Request getRequest() {
        return requestBuilder.build();
    }

    private void init() {
        set("addHeader", args -> {
            Arguments.check(2, args.length);
            requestBuilder.addHeader(args[0].asString(), args[1].asString());
            return this;
        });
        set("cacheControl", args -> {
            Arguments.check(1, args.length);
            // TODO
            return this;
        });
        set("delete", httpMethod(requestBuilder::delete, requestBuilder::delete));
        set("get", args -> {
            requestBuilder.get();
            return this;
        });
        set("head", args -> {
            requestBuilder.head();
            return this;
        });
        set("header", args -> {
            Arguments.check(2, args.length);
            requestBuilder.header(args[0].asString(), args[1].asString());
            return this;
        });
        set("headers", args -> {
            Arguments.check(1, args.length);
            requestBuilder.headers(Values.getHeaders(args[0], " at first argument"));
            return this;
        });
        set("method", args -> {
            Arguments.checkOrOr(1, 2, args.length);
            final RequestBody body;
            if (args.length == 1) {
                body = null;
            } else {
                body = Values.getRequestBody(args[1], " at second argument");
            }
            requestBuilder.method(args[0].asString(), body);
            return this;
        });
        set("newCall", args -> {
            Arguments.check(1, args.length);
            final OkHttpClient client = Values.getHttpClient(args[0], " at first argument");
            return new CallValue(client.newCall(getRequest()));
        });
        set("patch", httpMethod(requestBuilder::patch));
        set("post", httpMethod(requestBuilder::post));
        set("put", httpMethod(requestBuilder::put));
        set("removeHeader", args -> {
            Arguments.check(1, args.length);
            requestBuilder.removeHeader(args[0].asString());
            return this;
        });
        set("url", args -> {
            Arguments.check(1, args.length);
            requestBuilder.url(args[0].asString());
            return this;
        });
    }
    
    private Function httpMethod(VoidToVoidFunction voidFunc, RequestBodyToVoidFunction bodyFunc) {
        return (args) -> {
            Arguments.checkOrOr(0, 1, args.length);
            if (args.length == 0) {
                voidFunc.apply();
            } else {
                bodyFunc.apply(Values.getRequestBody(args[0], " at first argument"));
            }
            return this;
        };
    }

    private Function httpMethod(RequestBodyToVoidFunction bodyFunc) {
        return (args) -> {
            Arguments.check(1, args.length);
            bodyFunc.apply(Values.getRequestBody(args[0], " at first argument"));
            return this;
        };
    }

    private interface RequestBodyToVoidFunction {
        void apply(RequestBody value);
    }
}
