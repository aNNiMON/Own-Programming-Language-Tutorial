package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.ast.*;

public final class UseWithNonStringValueValidator extends LintVisitor {

    @Override
    public void visit(IncludeStatement st) {
        super.visit(st);
        applyVisitor(st, this);
    }

    @Override
    public void visit(UseStatement st) {
        super.visit(st);

        if (st.expression instanceof ArrayExpression) {
            ArrayExpression ae = (ArrayExpression) st.expression;
            for (Expression expr : ae.elements) {
                if (!checkExpression(expr)) {
                    return;
                }
            }
        } else {
            if (!checkExpression(st.expression)) {
                return;
            }
        }
    }

    private boolean checkExpression(Expression expr) {
        if (!(expr instanceof ValueExpression)) {
            Console.error(String.format(
                    "Warning: `use` with %s, not ValueExpression", expr.getClass().getSimpleName()));
            return false;
        }

        final Value value = ((ValueExpression) expr).value;
        if (value.type() != Types.STRING) {
            warnWrongType(value);
            return false;
        }
        return true;
    }

    private void warnWrongType(Value value) {
        Console.error(String.format(
                "Warning: `use` with %s - %s, not string",
                Types.typeToString(value.type()), value.asString()));
    }
}
