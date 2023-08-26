package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.ArrayExpression;
import com.annimon.ownlang.parser.ast.Expression;
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
        if (st.expression instanceof ArrayExpression) {
            ArrayExpression ae = (ArrayExpression) st.expression;
            for (Expression expr : ae.elements) {
                modules.add(expr.eval().asString());
            }
        }
        if (st.expression instanceof ValueExpression) {
            ValueExpression ve = (ValueExpression) st.expression;
            modules.add(ve.value.asString());
        }
        super.visit(st);
    }
}
