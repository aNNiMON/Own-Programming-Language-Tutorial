package com.annimon.ownlang.docs;

import com.annimon.ownlang.Version;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import java.util.*;
import java.util.stream.Collectors;

public class ModuleInfo {
    private final String name;
    final List<String> functions;
    final Map<String, Value> constants;
    final List<String> types;

    public ModuleInfo(String name) {
        this.name = name;
        functions = new ArrayList<>();
        constants = new HashMap<>();
        types = new ArrayList<>();
    }

    public List<Map<String, Object>> functions() {
        return functions.stream().sorted()
                .map(f -> {
                    final Map<String, Object> function = new LinkedHashMap<>();
                    function.put("name", f);
                    function.put("args", "");
                    function.put("desc", "");
                    function.put("desc_ru", "");
                    return function;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> constants() {
        final List<Map<String, Object>> result = new ArrayList<>();
        constants.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    final Value value = entry.getValue();

                    final Map<String, Object> constant = new LinkedHashMap<>();
                    constant.put("name", entry.getKey());
                    constant.put("type", value.type());
                    constant.put("typeName", Types.typeToString(value.type()));
                    if (value.type() == Types.MAP) {
                        String text = ((MapValue) value).getMap().entrySet().stream()
                                .sorted(Comparator.comparing(
                                        e -> ((MapValue) value).size() > 16 ? e.getKey() : e.getValue()))
                                .map(Object::toString)
                                .collect(Collectors.joining(", ", "{", "}"));
                        constant.put("value", text);
                    } else {
                        constant.put("value", value.asString());
                    }
                    result.add(constant);
                });
        return result;
    }

    public Map<String, Object> info() {
        final Map<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("scope", "both");
        result.put("since", "%d.%d.%d".formatted(Version.VERSION_MAJOR, Version.VERSION_MINOR, Version.VERSION_PATCH));
        result.put("constants", constants());
        result.put("functions", functions());
        if (!types.isEmpty()) {
            result.put("types", types.stream().sorted()
                    .map(s -> {
                        final Map<String, String> type = new HashMap<>();
                        type.put("name", s);
                        return type;
                    })
                    .toArray());
        }
        return result;
    }
}