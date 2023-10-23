package com.annimon.ownlang.lib;

import java.util.List;

public record ClassDeclaration(
        String name,
        List<ClassField> classFields,
        List<ClassMethod> classMethods) implements Instantiable {

    /**
     * Create an instance and put evaluated fields with method declarations
     * @return new {@link ClassInstance}
     */
    public ClassInstance newInstance(Value[] args) {
        final var instance = new ClassInstance(name);
        for (ClassField f : classFields) {
            instance.addField(f);
        }
        for (ClassMethod m : classMethods) {
            instance.addMethod(m);
        }
        return instance.callConstructor(args);
    }
}
