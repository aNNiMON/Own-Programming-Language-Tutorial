package com.annimon.ownlang.modules.yaml;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public final class yaml_encode implements Function {

    @Override
    public Value execute(Value[] args) {
        Arguments.checkOrOr(1, 2, args.length);
        try {
            final Object root = process(args[0]);
            final DumperOptions options = new DumperOptions();
            if (args.length == 2 && args[1].type() == Types.MAP) {
                configure(options, ((MapValue) args[1]));
            }
            final String yamlRaw = new Yaml(options).dump(root);
            return new StringValue(yamlRaw);
        } catch (Exception ex) {
            throw new OwnLangRuntimeException("Error while creating yaml", ex);
        }
    }

    private void configure(DumperOptions options, MapValue map) {
        map.ifPresent("allowReadOnlyProperties", value ->
                options.setAllowReadOnlyProperties(value.asInt() != 0));
        map.ifPresent("allowUnicode", value ->
                options.setAllowUnicode(value.asInt() != 0));
        map.ifPresent("canonical", value ->
                options.setCanonical(value.asInt() != 0));
        map.ifPresent("defaultFlowStyle", value ->
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.valueOf(value.asString())));
        map.ifPresent("defaultScalarStyle", value ->
                options.setDefaultScalarStyle(DumperOptions.ScalarStyle.valueOf(value.asString())));
        map.ifPresent("explicitEnd", value ->
                options.setExplicitEnd(value.asInt() != 0));
        map.ifPresent("explicitStart", value ->
                options.setExplicitStart(value.asInt() != 0));
        map.ifPresent("indent", value ->
                options.setIndent(value.asInt()));
        map.ifPresent("indicatorIndent", value ->
                options.setIndicatorIndent(value.asInt()));
        map.ifPresent("lineBreak", value ->
                options.setLineBreak(DumperOptions.LineBreak.valueOf(value.asString())));
        map.ifPresent("prettyFlow", value ->
                options.setPrettyFlow(value.asInt() != 0));
        map.ifPresent("splitLines", value ->
                options.setSplitLines(value.asInt() != 0));
        map.ifPresent("width", value ->
                options.setWidth(value.asInt()));
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