package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Values {

    public static RequestBody getRequestBody(Value arg, String msg) {
        if (arg.type() == Types.MAP && (arg instanceof RequestBodyValue)) {
            return ((RequestBodyValue) arg).getRequestBody();
        }
        throw new TypeException("RequestBody value expected" + msg);
    }

    public static Request getRequest(Value arg, String msg) {
        if (arg.type() == Types.MAP && (arg instanceof RequestBuilderValue)) {
            return ((RequestBuilderValue) arg).getRequest();
        }
        throw new TypeException("Request value expected" + msg);
    }

    public static OkHttpClient getHttpClient(Value arg, String msg) {
        if (arg.type() == Types.MAP && (arg instanceof HttpClientValue)) {
            return ((HttpClientValue) arg).getClient();
        }
        throw new TypeException("HttpClient value expected" + msg);
    }

    public static Headers getHeaders(Value arg, String msg) {
        if (arg.type() != Types.MAP) {
            throw new TypeException("Map expected" + msg);
        }
        final Map<String, String> headers = new LinkedHashMap<>();
        for (Map.Entry<Value, Value> entry : ((MapValue) arg)) {
            headers.put(entry.getKey().asString(), entry.getValue().asString());
        }
        return Headers.of(headers);
    }
}
