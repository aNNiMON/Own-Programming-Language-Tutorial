package com.annimon.ownlang.modules.yaml;

import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public final class yaml_encode implements Function {

    @Override
    public Value execute(Value... args) {
        Arguments.check(1, args.length);
        try {
            final Object root = process(args[0]);
            final String yamlRaw = new Yaml().dump(root);
            return new StringValue(yamlRaw);
        } catch (Exception ex) {
            throw new RuntimeException("Error while creating yaml", ex);
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
                return null;
        }
    }
    
    private Object process(MapValue map) {
        final Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<Value, Value> entry : map) {
            final String key = entry.getKey().asString();
            final Object value = process(entry.getValue());
            result.put(key, value);
        }
        return result;
    }
    
    private Object process(ArrayValue array) {
        final List<Object> result = new ArrayList<>();
        for (Value value : array) {
            result.add(process(value));
        }
        return result;
    }
}