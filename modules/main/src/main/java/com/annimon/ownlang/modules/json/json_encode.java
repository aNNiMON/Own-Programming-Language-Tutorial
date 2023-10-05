package com.annimon.ownlang.modules.json;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.Arguments;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Function;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.ValueUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONWriter;


public final class json_encode implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        try {
            final int indent;
            if (args.length == 2) {
                indent = args[1].asInt();
            } else {
                indent = 0;
            }

            final String jsonRaw;
            if (indent > 0) {
                jsonRaw = format(args[0], indent);
            } else {
                final Object root = ValueUtils.toObject(args[0]);
                jsonRaw = JSONWriter.valueToString(root);
            }
            return new StringValue(jsonRaw);

        } catch (JSONException ex) {
            throw new OwnLangRuntimeException("Error while creating json", ex);
        }
    }

    private String format(Value val, int indent) {
        switch (val.type()) {
            case Types.ARRAY:
                return ValueUtils.toObject((ArrayValue) val).toString(indent);
            case Types.MAP:
                return ValueUtils.toObject((MapValue) val).toString(indent);
            case Types.NUMBER:
                return JSONWriter.valueToString(val.raw());
            case Types.STRING:
                return JSONWriter.valueToString(val.asString());
            default:
                return JSONWriter.valueToString(JSONObject.NULL);
        }
    }
}