package com.annimon.ownlang.lib;

import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ValueUtils {

    public static Object toObject(Value val) {
        switch (val.type()) {
            case Types.ARRAY:
                return toObject((ArrayValue) val);
            case Types.MAP:
                return toObject((MapValue) val);
            case Types.NUMBER:
                return val.raw();
            case Types.STRING:
                return val.asString();
            default:
                return JSONObject.NULL;
        }
    }

    public static Object toObject(MapValue map) {
        final JSONObject result = new JSONObject();
        for (Map.Entry<Value, Value> entry : map) {
            final String key = entry.getKey().asString();
            final Object value = toObject(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

    public static Object toObject(ArrayValue array) {
        final JSONArray result = new JSONArray();
        for (Value value : array) {
            result.put(toObject(value));
        }
        return result;
    }

    public static Value toValue(Object obj) {
        if (obj instanceof JSONObject) {
            return toValue((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            return toValue((JSONArray) obj);
        }
        if (obj instanceof String) {
            return new StringValue((String) obj);
        }
        if (obj instanceof Number) {
            return NumberValue.of(((Number) obj));
        }
        if (obj instanceof Boolean) {
            return NumberValue.fromBoolean((Boolean) obj);
        }
        // NULL or other
        return NumberValue.ZERO;
    }

    public static MapValue toValue(JSONObject json) {
        final MapValue result = new MapValue(json.length());
        final Iterator<String> it = json.keys();
        while(it.hasNext()) {
            final String key = it.next();
            final Value value = toValue(json.get(key));
            result.set(new StringValue(key), value);
        }
        return result;
    }

    public static ArrayValue toValue(JSONArray json) {
        final int length = json.length();
        final ArrayValue result = new ArrayValue(length);
        for (int i = 0; i < length; i++) {
            final Value value = toValue(json.get(i));
            result.set(i, value);
        }
        return result;
    }

    public static Number getNumber(Value value) {
        if (value.type() == Types.NUMBER) return ((NumberValue) value).raw();
        return value.asInt();
    }

    public static float getFloatNumber(Value value) {
        if (value.type() == Types.NUMBER) return ((NumberValue) value).raw().floatValue();
        return (float) value.asNumber();
    }
}
