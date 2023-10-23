package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.EvaluableValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class AssignmentExpression extends InterruptableNode implements Statement, EvaluableValue {

    public final Accessible target;
    public final BinaryExpression.Operator operation;
    public final Node expression;
    
    public AssignmentExpression(BinaryExpression.Operator operation, Accessible target, Node expr) {
        this.operation = operation;
        this.target = target;
        this.expression = expr;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        if (operation == null) {
            // Simple assignment
            return target.set(expression.eval());
        }
        final Node expr1 = new ValueExpression(target.get());
        final Node expr2 = new ValueExpression(expression.eval());
        return target.set(new BinaryExpression(operation, expr1, expr2).eval());
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        final String op = (operation == null) ? "" : operation.toString();
        return String.format("%s %s= %s", target, op, expression);
    }
}
