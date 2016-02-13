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
        if (Variables.isExists(s.variable)) {
            throw new RuntimeException("Cannot assign value to constant");
        }
    }
}
