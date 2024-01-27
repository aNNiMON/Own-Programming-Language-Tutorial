package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.lib.EvaluableValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;

/**
 *
 * @author aNNiMON
 */
public final class AssignmentExpression extends InterruptableNode implements Statement, EvaluableValue, SourceLocation {

    public final Accessible target;
    public final BinaryExpression.Operator operation;
    public final Node expression;
    private final Range range;
    
    public AssignmentExpression(BinaryExpression.Operator operation, Accessible target, Node expr, Range range) {
        this.operation = operation;
        this.target = target;
        this.expression = expr;
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        if (operation == null) {
            // Simple assignment
            return target.set(checkNonNull(expression.eval(), "Assignment expression"));
        }
        final Node expr1 = new ValueExpression(checkNonNull(target.get(), "Assignment target"));
        final Node expr2 = new ValueExpression(checkNonNull(expression.eval(), "Assignment expression"));
        final Value result = new BinaryExpression(operation, expr1, expr2).eval();
        return target.set(result);
    }

    private Value checkNonNull(Value value, String message) {
        if (value == null) {
            throw new OwnLangRuntimeException(message + " evaluates to null", range);
        }
        return value;
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
