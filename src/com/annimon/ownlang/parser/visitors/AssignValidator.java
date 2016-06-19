package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.ast.*;

/**
 *
 * @author aNNiMON
 */
public final class AssignValidator extends LintVisitor {

    @Override
    public void visit(AssignmentExpression s) {
        super.visit(s);
        if (s.target instanceof VariableExpression) {
            final String variable = ((VariableExpression) s.target).name;
            if (Variables.isExists(variable)) {
                Console.error(String.format(
                    "Warning: variable \"%s\" overrides constant", variable));
            }
        }
    }

    @Override
    public void visit(IncludeStatement st) {
        super.visit(st);
        applyVisitor(st, this);
    }

    @Override
    public void visit(UseStatement st) {
        super.visit(st);
        st.execute();
    }
}
