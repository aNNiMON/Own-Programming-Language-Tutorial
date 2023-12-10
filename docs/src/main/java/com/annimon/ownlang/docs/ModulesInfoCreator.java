package com.annimon.ownlang.docs;

import com.annimon.ownlang.lib.ModuleLoader;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.modules.Module;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ModulesInfoCreator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No modules provided.\nUsage: ModulesInfoCreator <module1> <module2> ...");
            System.exit(1);
        }

        final Class<Module> clazz = Module.class; // get classloader for package

        final List<ModuleInfo> moduleInfos = new ArrayList<>();

        for (String moduleName : args) {
            final Module module = ModuleLoader.load(moduleName);

            final ModuleInfo moduleInfo = new ModuleInfo(moduleName);
            moduleInfo.functions.addAll(module.functions().keySet());
            moduleInfo.constants.putAll(module.constants());
            moduleInfo.types.addAll(listValues(module.getClass()));
            moduleInfos.add(moduleInfo);
        }

        printAsYaml(moduleInfos);

        System.out.println("Total functions: " + moduleInfos.stream()
                .mapToLong(m -> m.functions.size())
                .sum()
        );
        System.out.println("Total constants: " + moduleInfos.stream()
                .mapToLong(m -> m.constants.keySet().size())
                .sum()
        );
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

    private static List<String> listValues(Class<?> moduleClass) {
        return Arrays.stream(moduleClass.getDeclaredClasses())
                .filter(clazz -> getAllInterfaces(clazz).stream().anyMatch(i -> i.equals(Value.class)))
                .map(Class::getSimpleName)
                .collect(Collectors.toList());
    }

    private static Set<Class<?>> getAllInterfaces(Class<?> clazz) {
        if (clazz.getSuperclass() == null) {
            return Collections.emptySet();
        }
        return Stream.concat(Arrays.stream(clazz.getInterfaces()), getAllInterfaces(clazz.getSuperclass()).stream())
                .collect(Collectors.toSet());
    }

}
