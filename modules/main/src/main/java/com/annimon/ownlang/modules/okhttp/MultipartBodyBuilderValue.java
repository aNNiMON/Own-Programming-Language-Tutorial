package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MultipartBodyBuilderValue extends MapValue {

    private final MultipartBody.Builder builder;

    public MultipartBodyBuilderValue() {
        super(5);
        this.builder = new MultipartBody.Builder();
        init();
    }

    private void init() {
        set("addFormData", this::addFormData);
        set("addFormDataPart", this::addFormDataPart);
        set("addPart", this::addPart);
        set("build", args -> new MultipartBodyValue(builder.build()));
        set("setType", args -> {
            Arguments.check(1, args.length);
            builder.setType(MediaType.parse(args[0].asString()));
            return this;
        });
    }

    private Value addFormDataPart(Value[] args) {
        Arguments.checkOrOr(2, 3, args.length);
        if (args.length == 2) {
            builder.addFormDataPart(args[0].asString(), args[1].asString());
        } else {
            builder.addFormDataPart(
                    args[0].asString(),
                    args[1].asString(),
                    Values.getRequestBody(args[2], " at third argument"));
        }
        return this;
    }

    private Value addFormData(Value[] args) {
        Arguments.check(1, args.length);
        if (args[0].type() != Types.MAP) {
            throw new TypeException("Map expected at first argument");
        }
        for (Map.Entry<Value, Value> entry : ((MapValue) args[0])) {
            builder.addFormDataPart(entry.getKey().asString(), entry.getValue().asString());
        }
        return this;
    }

    private Value addPart(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        RequestBody requestBody = Values.getRequestBody(args[0], " at first argument");
        if (args.length == 1) {
            builder.addPart(requestBody);
        } else {
            builder.addPart(
                    Values.getHeaders(args[1], " at second argument"),
                    requestBody);
        }
        return this;
    }
}
