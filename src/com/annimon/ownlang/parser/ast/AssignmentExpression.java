package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;

/**
 *
 * @author aNNiMON
 */
public final class AssignmentExpression implements Expression {

    public final String variable;
    public final Expression expression;

    public AssignmentExpression(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }
    
    @Override
    public Value eval() {
        final Value result = expression.eval();
        Variables.set(variable, result);
        return result;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s = %s", variable, expression);
    }
}
