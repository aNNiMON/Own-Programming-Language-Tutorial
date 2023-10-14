package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class ContinueStatement extends RuntimeException implements Statement {

    @Override
    public Value eval() {
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
        return "continue";
    }
}
