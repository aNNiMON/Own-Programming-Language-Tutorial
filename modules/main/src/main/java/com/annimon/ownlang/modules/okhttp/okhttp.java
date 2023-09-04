package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public final class okhttp implements Module {

    private static final HttpClientValue defaultClient = new HttpClientValue(new OkHttpClient());

    public static void initConstants() {
        MapValue requestBody = new MapValue(5);
        requestBody.set("bytes", args -> {
            Arguments.checkOrOr(2, 4, args.length);
            if (args[1].type() != Types.ARRAY) {
                throw new TypeException("Array of bytes expected at second argument");
            }
            final byte[] bytes = ValueUtils.toByteArray((ArrayValue) args[1]);
            final int offset;
            final int bytesCount;
            if (args.length == 2) {
                offset = 0;
                bytesCount = bytes.length;
            } else {
                offset = args[2].asInt();
                bytesCount = args[3].asInt();
            }
            return new RequestBodyValue(RequestBody.create(
                    MediaType.parse(args[0].asString()),
                    bytes, offset, bytesCount
            ));
        });
        requestBody.set("file", args -> {
            Arguments.check(2, args.length);
            return new RequestBodyValue(RequestBody.create(
                    MediaType.parse(args[0].asString()),
                    Console.fileInstance(args[1].asString())
            ));
        });
        requestBody.set("string", args -> {
            Arguments.check(2, args.length);
            return new RequestBodyValue(RequestBody.create(
                    MediaType.parse(args[0].asString()),
                    args[1].asString()
            ));
        });
        ScopeHandler.setConstant("RequestBody", requestBody);


        MapValue multipartBody = new MapValue(10);
        multipartBody.set("ALTERNATIVE", new StringValue(MultipartBody.ALTERNATIVE.toString()));
        multipartBody.set("DIGEST", new StringValue(MultipartBody.DIGEST.toString()));
        multipartBody.set("FORM", new StringValue(MultipartBody.FORM.toString()));
        multipartBody.set("MIXED", new StringValue(MultipartBody.MIXED.toString()));
        multipartBody.set("PARALLEL", new StringValue(MultipartBody.PARALLEL.toString()));
        multipartBody.set("builder", args -> new MultipartBodyBuilderValue());
        ScopeHandler.setConstant("MultipartBody", multipartBody);


        MapValue okhttp = new MapValue(5);
        okhttp.set("client", defaultClient);
        okhttp.set("request", args -> new RequestBuilderValue());
        ScopeHandler.setConstant("okhttp", okhttp);
    }

    @Override
    public void init() {
        initConstants();
    }
}
