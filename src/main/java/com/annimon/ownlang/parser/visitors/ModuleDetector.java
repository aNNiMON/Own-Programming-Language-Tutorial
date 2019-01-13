package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.UseStatement;
import com.annimon.ownlang.parser.ast.ValueExpression;
import java.util.HashSet;
import java.util.Set;

public class ModuleDetector extends AbstractVisitor {

    private Set<String> modules;

    public ModuleDetector() {
        modules = new HashSet<>();
    }

    public Set<String> detect(Statement s) {
        s.accept(this);
        return modules;
    }

    @Override
    public void visit(UseStatement st) {
        if (st.expression instanceof ValueExpression) {
            ValueExpression ve = (ValueExpression) st.expression;
            if (ve.value.type() == Types.ARRAY) {
                for (Value module : ((ArrayValue) ve.value)) {
                    modules.add(module.asString());
                }
            } else {
                modules.add(ve.value.asString());
            }
        }
        super.visit(st);
    }
}
