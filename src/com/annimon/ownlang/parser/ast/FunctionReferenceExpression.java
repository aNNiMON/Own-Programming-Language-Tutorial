package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.*;

/**
 *
 * @author aNNiMON
 */
public final class FunctionReferenceExpression implements Expression {

    public final String name;

    public FunctionReferenceExpression(String name) {
        this.name = name;
    }

    @Override
    public FunctionValue eval() {
        return new FunctionValue(Functions.get(name));
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "::" + name;
    }
}
