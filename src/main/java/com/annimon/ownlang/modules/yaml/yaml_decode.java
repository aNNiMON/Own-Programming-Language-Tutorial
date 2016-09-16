package com.annimon.ownlang.modules.yaml;

import com.annimon.ownlang.lib.*;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public final class yaml_decode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        try {
            final String yamlRaw = args[0].asString();
            final Object root = new Yaml().load(yamlRaw);
            final Value process = process(root);
            return process;
        } catch (Exception ex) {
            throw new RuntimeException("Error while parsing yaml", ex);
        }
    }
    
    private Value process(Object obj) {
        if (obj instanceof Map) {
            return process((Map) obj);
        }
        if (obj instanceof List) {
            return process((List) obj);
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
    
    private MapValue process(Map<Object, Object> map) {
        final MapValue result = new MapValue(map.size());
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            final Value value = process(entry.getValue());
            result.set(new StringValue(key), value);
        }
        return result;
    }
    
    private ArrayValue process(List list) {
        final int length = list.size();
        final ArrayValue result = new ArrayValue(length);
        for (int i = 0; i < length; i++) {
            final Value value = process(list.get(i));
            result.set(i, value);
        }
        return result;
    }
}