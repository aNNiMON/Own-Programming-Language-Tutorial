package com.annimon.ownlang.lib.modules.functions;

import com.annimon.ownlang.lib.*;
import java.util.Iterator;
import org.json.*;

public final class json_decode implements Function {

    @Override
    public Value execute(Value... args) {
        if (args.length != 1) throw new RuntimeException("One argument expected");
        try {
            final String jsonRaw = args[0].asString();
            final Object root = new JSONTokener(jsonRaw).nextValue();
            return process(root);
        } catch (JSONException ex) {
            throw new RuntimeException("Error while parsing json", ex);
        }
    }
    
    private Value process(Object obj) {
        if (obj instanceof JSONObject) {
            return process((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            return process((JSONArray) obj);
        }
        if (obj instanceof String) {
            return new StringValue((String) obj);
        }
        if (obj instanceof Number) {
            return new NumberValue(((Number) obj).doubleValue());
        }
        if (obj instanceof Boolean) {
            return NumberValue.fromBoolean((Boolean) obj);
        }
        // NULL or other
        return NumberValue.ZERO;
    }
    
    private MapValue process(JSONObject json) {
        final MapValue result = new MapValue(json.length());
        final Iterator<String> it = json.keys();
        while(it.hasNext()) {
            final String key = it.next();
            final Value value = process(json.get(key));
            result.set(new StringValue(key), value);
        }
        return result;
    }
    
    private ArrayValue process(JSONArray json) {
        final int length = json.length();
        final ArrayValue result = new ArrayValue(length);
        for (int i = 0; i < length; i++) {
            final Value value = process(json.get(i));
            result.set(i, value);
        }
        return result;
    }
}