package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class IfStatement extends InterruptableNode implements Statement {
    
    public final Node expression;
    public final Statement ifStatement, elseStatement;

    public IfStatement(Node expression, Statement ifStatement, Statement elseStatement) {
        this.expression = expression;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }
    
    @Override
    public Value eval() {
        super.interruptionCheck();
        final int result = expression.eval().asInt();
        if (result != 0) {
            ifStatement.eval();
        } else if (elseStatement != null) {
            elseStatement.eval();
        }
        return NumberValue.ZERO;
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
        final StringBuilder result = new StringBuilder();
        result.append("if ").append(expression).append(' ').append(ifStatement);
        if (elseStatement != null) {
            result.append("\nelse ").append(elseStatement);
        }
        return result.toString();
    }
}
