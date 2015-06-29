package com.annimon.ownlang.parser.visitors;

import com.annimon.ownlang.parser.ast.*;

/**
 *
 * @author aNNiMON
 */
public final class VariablePrinter extends AbstractVisitor {

    @Override
    public void visit(ArrayAccessExpression s) {
        super.visit(s);
        System.out.println(s.variable);
    }
    
    @Override
    public void visit(AssignmentStatement s) {
        super.visit(s);
        System.out.println(s.variable);
    }

    @Override
    public void visit(VariableExpression s) {
        super.visit(s);
        System.out.println(s.name);
    }
}
