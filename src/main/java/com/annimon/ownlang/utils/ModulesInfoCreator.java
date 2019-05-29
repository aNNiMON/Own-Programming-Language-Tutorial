package com.annimon.ownlang.utils;

import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.modules.Module;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public final class ModulesInfoCreator {

    private static final String MODULES_PATH = "src/main/java/com/annimon/ownlang/modules";

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<Module> clazz = Module.class; // get classloader for package

        final List<ModuleInfo> moduleInfos = new ArrayList<>();

        String[] moduleNames = Optional.ofNullable(new File(MODULES_PATH).listFiles())
                .map(Arrays::stream)
                .orElse(Stream.empty())
                .filter(File::isDirectory)
                .map(File::getName)
                .toArray(String[]::new);
        for (String moduleName : moduleNames) {
            final String moduleClassPath = String.format("com.annimon.ownlang.modules.%s.%s", moduleName, moduleName);
            Class<?> moduleClass = Class.forName(moduleClassPath);
            Functions.getFunctions().clear();
            Variables.variables().clear();
            final Module module = (Module) moduleClass.newInstance();
            module.init();

            final ModuleInfo moduleInfo = new ModuleInfo(moduleName);
            moduleInfo.functions.addAll(Functions.getFunctions().keySet());
            moduleInfo.constants.putAll(Variables.variables());
            moduleInfo.types.addAll(listValues(moduleClass));
            moduleInfos.add(moduleInfo);
        }

        // printAsJson(moduleInfos);
        printAsYaml(moduleInfos);

        System.out.println("Total modules: " + moduleInfos.size());
        System.out.println("Total functions: " + moduleInfos.stream()
                .mapToLong(m -> m.functions.size())
                .sum()
        );
        System.out.println("Total constants: " + moduleInfos.stream()
                .mapToLong(m -> m.constants.keySet().size())
                .sum()
        );
    }

    private static void printAsJson(List<ModuleInfo> moduleInfos) {
        final JSONArray modulesJson = new JSONArray();
        for (ModuleInfo moduleInfo : moduleInfos) {
            modulesJson.put(new JSONObject(moduleInfo.info()));
        }
        System.out.println(modulesJson.toString(2));
    }

    private static void printAsYaml(List<ModuleInfo> moduleInfos) {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final List<Map<String, Object>> infos = new ArrayList<>();
        for (ModuleInfo moduleInfo : moduleInfos) {
            infos.add(moduleInfo.info());
        }
        System.out.println(new Yaml(options).dump(infos));
    }

    private static List<String> listValues(Class moduleClass) {
        return Arrays.stream(moduleClass.getDeclaredClasses())
                .filter(clazz -> getAllInterfaces(clazz).stream().anyMatch(i -> i.equals(Value.class)))
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }

    private static Set<Class> getAllInterfaces(Class clazz) {
        if (clazz.getSuperclass() == null) return Collections.emptySet();
        return Stream.concat(Arrays.stream(clazz.getInterfaces()), getAllInterfaces(clazz.getSuperclass()).stream())
                .collect(Collectors.toSet());
    }

    static class ModuleInfo {
        private final String name;
        List<String> functions;
        Map<String, Value> constants;
        List<String> types;

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
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(entry -> {
                final Value value = entry.getValue();

                final Map<String, Object> constant = new LinkedHashMap<>();
                constant.put("name", entry.getKey());
                constant.put("type", value.type());
                constant.put("typeName", Types.typeToString(value.type()));
                if (value.type() == Types.MAP) {
                    String text = ((MapValue) value).getMap().entrySet().stream()
                            .sorted(Comparator.comparing(
                                    e -> ((MapValue)value).size() > 16 ? e.getKey() : e.getValue()))
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
}
