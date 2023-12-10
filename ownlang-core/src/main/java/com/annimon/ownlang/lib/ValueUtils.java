package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.TypeException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ValueUtils {

    private ValueUtils() { }

    public static Object toObject(Value val) {
        return switch (val.type()) {
            case Types.ARRAY -> toObject((ArrayValue) val);
            case Types.MAP -> toObject((MapValue) val);
            case Types.NUMBER -> val.raw();
            case Types.STRING -> val.asString();
            default -> JSONObject.NULL;
        };
    }

    public static JSONObject toObject(MapValue map) {
        final JSONObject result = new JSONObject(new LinkedHashMap<String, Object>());
        for (Map.Entry<Value, Value> entry : map) {
            final String key = entry.getKey().asString();
            final Object value = toObject(entry.getValue());
            result.put(key, value);
        }
        return result;
    }

    public static JSONArray toObject(ArrayValue array) {
        final JSONArray result = new JSONArray();
        for (Value value : array) {
            result.put(toObject(value));
        }
        return result;
    }

    public static Value toValue(Object obj) {
        if (obj instanceof JSONObject jsonObj) {
            return toValue(jsonObj);
        }
        if (obj instanceof JSONArray jsonArr) {
            return toValue(jsonArr);
        }
        if (obj instanceof String str) {
            return new StringValue(str);
        }
        if (obj instanceof Number num) {
            return NumberValue.of(num);
        }
        if (obj instanceof Boolean flag) {
            return NumberValue.fromBoolean(flag);
        }
        // NULL or other
        return NumberValue.ZERO;
    }

    public static MapValue toValue(JSONObject json) {
        final MapValue result = new MapValue(new LinkedHashMap<>(json.length()));
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

    public static byte[] toByteArray(ArrayValue array) {
        final int size = array.size();
        final byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) array.get(i).asInt();
        }
        return result;
    }

    public static MapValue consumeMap(Value value, int argumentNumber) {
        final int type = value.type();
        if (type != Types.MAP) {
            throw new TypeException("Map expected at argument " + (argumentNumber + 1)
                    + ", but found " + Types.typeToString(type));
        }
        return (MapValue) value;
    }

    public static Function consumeFunction(Value value, int argumentNumber) {
        return consumeFunction(value, " at argument " + (argumentNumber + 1));
    }

    public static Function consumeFunction(Value value, String errorMessage) {
        final int type = value.type();
        if (type != Types.FUNCTION) {
            throw new TypeException("Function expected" + errorMessage
                    + ", but found " + Types.typeToString(type));
        }
        return ((FunctionValue) value).getValue();
    }

    public static <T extends Number> MapValue collectNumberConstants(Class<?> clazz, Class<T> type) {
        return collectConstants(clazz, type, NumberValue::of);
    }

    public static <T extends String> MapValue collectStringConstants(Class<?> clazz) {
        return collectConstants(clazz, String.class, StringValue::new);
    }

    @SuppressWarnings("unchecked")
    private static <T, V extends Value> MapValue collectConstants(Class<?> clazz, Class<T> type, FieldConverter<? super T, ? extends V> converter) {
        MapValue result = new MapValue(20);
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!field.getType().equals(type)) continue;
            try {
                result.set(field.getName(), converter.convert((T) field.get(type)));
            } catch (IllegalAccessException ignore) {
            }
        }
        return result;
    }

    private interface FieldConverter<T, V> {
        V convert(T input) throws IllegalAccessException;
    }
}
