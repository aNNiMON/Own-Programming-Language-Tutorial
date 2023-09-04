package com.annimon.ownlang.modules.http;

import com.annimon.ownlang.lib.*;
import java.io.IOException;
import okhttp3.*;

public final class http_download implements Function {
    
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Value execute(Value[] args) {
        Arguments.check(1, args.length);
        try {
            final Response response = client.newCall(
                    new Request.Builder().url(args[0].asString()).build())
                    .execute();
            return ArrayValue.of(response.body().bytes());
        } catch (IOException ex) {
            return new ArrayValue(0);
        }
    }
}