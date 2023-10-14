package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class ReturnStatement extends RuntimeException implements Statement {

    public final Node expression;
    private Value result;
    
    public ReturnStatement(Node expression) {
        this.expression = expression;
    }
    
    public Value getResult() {
        return result;
    }
    
    @Override
    public Value eval() {
        result = expression.eval();
        throw this;
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
        return "return " + expression;
    }
}
