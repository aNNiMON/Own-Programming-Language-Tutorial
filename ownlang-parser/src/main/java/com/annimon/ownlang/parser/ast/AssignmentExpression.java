package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class AssignmentExpression extends InterruptableNode implements Expression, Statement {

    public final Accessible target;
    public final BinaryExpression.Operator operation;
    public final Expression expression;
    
    public AssignmentExpression(BinaryExpression.Operator operation, Accessible target, Expression expr) {
        this.operation = operation;
        this.target = target;
        this.expression = expr;
    }
    
    @Override
    public void execute() {
        eval();
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        if (operation == null) {
            // Simple assignment
            return target.set(expression.eval());
        }
        final Expression expr1 = new ValueExpression(target.get());
        final Expression expr2 = new ValueExpression(expression.eval());
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
