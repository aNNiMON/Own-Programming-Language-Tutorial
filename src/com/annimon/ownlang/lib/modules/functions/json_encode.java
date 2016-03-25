package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;
import java.util.Map;
import org.json.*;

public final class json_encode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        try {
            final Object root = process(args[0]);
            final String jsonRaw = JSONObject.valueToString(root);
            return new StringValue(jsonRaw);
        } catch (JSONException ex) {
            throw new RuntimeException("Error while creating json", ex);
        }
    }
    
    private Object process(Value val) {
        switch (val.type()) {
            case Types.ARRAY:
                return process((ArrayValue) val);
            case Types.MAP:
                return process((MapValue) val);
            case Types.NUMBER:
                return val.raw();
            case Types.STRING:
                return val.asString();
            default:
                return JSONObject.NULL;
        }
    }
    
    private Object process(MapValue map) {
        final JSONObject result = new JSONObject();
        for (Map.Entry<Value, Value> entry : map) {
            final String key = entry.getKey().asString();
            final Object value = process(entry.getValue());
            result.put(key, value);
        }
        return result;
    }
    
    private Object process(ArrayValue array) {
        final JSONArray result = new JSONArray();
        for (Value value : array) {
            result.put(process(value));
        }
        return result;
    }
}