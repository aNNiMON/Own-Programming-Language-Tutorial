package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Converters;
import okhttp3.MultipartBody;

public class MultipartBodyValue extends RequestBodyValue {

    private final MultipartBody multipartBody;

    public MultipartBodyValue(MultipartBody multipartBody) {
        super(multipartBody, 5);
        this.multipartBody = multipartBody;
        init();
    }

    public MultipartBody getMultipartBody() {
        return multipartBody;
    }

    private void init() {
        set("boundary", Converters.voidToString(multipartBody::boundary));
        set("size", Converters.voidToInt(multipartBody::size));
    }
}
