package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.VariableDoesNotExistsException;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;

/**
 *
 * @author aNNiMON
 */
public final class VariableExpression extends InterruptableNode implements Accessible, SourceLocation {
    
    public final String name;
    private Range range;
    
    public VariableExpression(String name) {
        this.name = name;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        return get();
    }
    
    @Override
    public Value get() {
        if (!ScopeHandler.isVariableOrConstantExists(name)) {
            throw new VariableDoesNotExistsException(name, getRange());
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
