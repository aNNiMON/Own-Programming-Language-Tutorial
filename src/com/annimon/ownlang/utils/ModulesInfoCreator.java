package com.annimon.ownlang.utils;

import com.annimon.ownlang.annotations.Modules;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.lib.modules.Module;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public final class ModulesInfoCreator {

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        final Class<Module> clazz = Module.class; // get classloader for package

        final List<ModuleInfo> moduleInfos = new ArrayList<>();

        final Package modulesPackage = Package.getPackage("com.annimon.ownlang.lib.modules");
        final Modules annotation = modulesPackage.getAnnotation(Modules.class);
        for (Class moduleClass : annotation.modules()) {
            Functions.getFunctions().clear();
            Variables.variables().clear();
            final Module module = (Module) moduleClass.newInstance();
            module.init();

            final ModuleInfo moduleInfo = new ModuleInfo(moduleClass.getSimpleName());
            moduleInfo.functions.addAll(Functions.getFunctions().keySet());
            moduleInfo.constants.putAll(Variables.variables());
            moduleInfos.add(moduleInfo);
        }

        final JSONArray modulesJson = new JSONArray();
        for (ModuleInfo moduleInfo : moduleInfos) {
            modulesJson.put(moduleInfo.toJSON());
        }
        System.out.println(modulesJson.toString(2));

        System.out.println("Total modules: " + moduleInfos.size());
        System.out.println("Total functions: " + moduleInfos.stream()
                .flatMap(m -> m.functions.stream())
                .count()
        );
        System.out.println("Total constants: " + moduleInfos.stream()
                .flatMap(m -> m.constants.keySet().stream())
                .count()
        );
    }

    static class ModuleInfo {
        private final String name;
        List<String> functions;
        Map<String, Value> constants;

        public ModuleInfo(String name) {
            this.name = name;
            functions = new ArrayList<>();
            constants = new HashMap<>();
        }

        public JSONArray constantsJSON() {
            final JSONArray result = new JSONArray();
            constants.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey()))
                    .forEach(entry -> {
                final Value value = entry.getValue();

                final JSONObject constant = new JSONObject();
                constant.put("name", entry.getKey());
                constant.put("type", value.type());
                constant.put("typeName", Types.typeToString(value.type()));
                constant.put("value", value.asString());
                result.put(constant);
            });
            return result;
        }

        public JSONObject toJSON() {
            final JSONObject json = new JSONObject();
            json.put("name", name);
            json.put("functions", functions.stream().sorted().toArray());
            json.put("constants", constantsJSON());
            return json;
        }
    }
}
