package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;

/**
 *
 * @author aNNiMON
 */
public final class BreakStatement extends RuntimeException implements Statement, SourceLocation {
    private final Range range;

    public BreakStatement(Range range) {
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

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
        return "break";
    }
}
