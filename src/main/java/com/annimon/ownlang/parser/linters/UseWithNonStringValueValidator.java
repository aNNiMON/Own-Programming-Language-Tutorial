package com.annimon.ownlang.parser.linters;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.ArrayValue;
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
        if (!(st.expression instanceof ValueExpression)) {
            Console.error(String.format(
                    "Warning: `use` with %s, not ValueExpression", st.expression.getClass().getSimpleName()));
            return;
        }

        final Value value = ((ValueExpression) st.expression).value;
        switch (value.type()) {
            case Types.STRING:
                // ok
                break;
            case Types.ARRAY:
                // ok, need additional check
                for (Value module : ((ArrayValue) value)) {
                    if (module.type() != Types.STRING) {
                        warnWrongType(module);
                    }
                }
                break;
            default:
                warnWrongType(value);
        }
    }

    private void warnWrongType(Value value) {
        Console.error(String.format(
                "Warning: `use` with %s - %s, not string",
                Types.typeToString(value.type()), value.asString()));
    }
}
