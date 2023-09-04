package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.*;

/**
 *
 * @author aNNiMON
 */
public final class FunctionReferenceExpression extends InterruptableNode implements Expression {

    public final String name;

    public FunctionReferenceExpression(String name) {
        this.name = name;
    }

    @Override
    public FunctionValue eval() {
        super.interruptionCheck();
        return new FunctionValue(ScopeHandler.getFunction(name));
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
        return "::" + name;
    }
}
