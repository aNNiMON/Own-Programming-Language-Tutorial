package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.UseStatement;
import java.util.HashSet;
import java.util.Set;

public class ModuleDetector extends AbstractVisitor {

    private final Set<String> modules;

    public ModuleDetector() {
        modules = new HashSet<>();
    }

    public Set<String> detect(Node s) {
        s.accept(this);
        return modules;
    }

    @Override
    public void visit(UseStatement s) {
        modules.addAll(s.modules);
        super.visit(s);
    }
}
