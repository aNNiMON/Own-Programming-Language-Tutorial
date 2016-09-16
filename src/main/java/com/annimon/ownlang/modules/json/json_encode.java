package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import org.json.JSONException;
import org.json.JSONObject;


public final class json_encode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        try {
            final Object root = ValueUtils.toObject(args[0]);
            final String jsonRaw = JSONObject.valueToString(root);
            return new StringValue(jsonRaw);
        } catch (JSONException ex) {
            throw new RuntimeException("Error while creating json", ex);
        }
    }
}