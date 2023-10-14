package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.VariableDoesNotExistsException;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class VariableExpression extends InterruptableNode implements Accessible {
    
    public final String name;
    
    public VariableExpression(String name) {
        this.name = name;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        return get();
    }
    
    @Override
    public Value get() {
        if (!ScopeHandler.isVariableOrConstantExists(name)) {
            throw new VariableDoesNotExistsException(name);
        }
        return ScopeHandler.getVariableOrConstant(name);
    }

    @Override
    public Value set(Value value) {
        ScopeHandler.setVariable(name, value);
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
        return name;
    }
}
