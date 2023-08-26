package com.annimon.ownlang.modules.okhttp;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Converters;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CallValue extends MapValue {

    private final Call call;

    public CallValue(Call call) {
        super(6);
        this.call = call;
        init();
    }

    private void init() {
        set("cancel", Converters.voidToVoid(call::cancel));
        set("enqueue", this::enqueue);
        set("execute", this::execute);
        set("isCanceled", Converters.voidToBoolean(call::isCanceled));
        set("isExecuted", Converters.voidToBoolean(call::isExecuted));
    }

    private Value enqueue(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        final Function onResponse = ValueUtils.consumeFunction(args[0], 0);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                onResponse.execute(new CallValue(call), new ResponseValue(response));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (args.length == 2) {
                    ValueUtils.consumeFunction(args[1], 1)
                            .execute(new CallValue(call), new StringValue(e.getMessage()));
                }
            }
        });
        return NumberValue.ZERO;
    }

    private Value execute(Value[] args) {
        Arguments.check(0, args.length);
        try {
            return new ResponseValue(call.execute());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
