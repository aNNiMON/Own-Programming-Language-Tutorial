package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.ast.*;

/**
 *
 * @author aNNiMON
 */
public final class AssignValidator extends AbstractVisitor {

    @Override
    public void visit(AssignmentExpression s) {
        super.visit(s);
        if (s.target instanceof VariableExpression) {
            final String variable = ((VariableExpression) s.target).name;
            if (Variables.isExists(variable)) {
                throw new RuntimeException("Cannot assign value to constant");
            }
        }
    }
}
