package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 * Wrapper for expressions, which can be used as statements.
 * 
 * @author aNNiMON
 */
public final class ExprStatement extends InterruptableNode implements Statement {
    
    public final Node expr;
    
    public ExprStatement(Node function) {
        this.expr = function;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        return expr.eval();
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
        return expr.toString();
    }
}
