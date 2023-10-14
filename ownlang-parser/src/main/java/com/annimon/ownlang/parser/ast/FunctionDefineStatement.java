package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.lib.UserDefinedFunction;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;

/**
 *
 * @author aNNiMON
 */
public final class FunctionDefineStatement implements Statement, SourceLocation {
    
    public final String name;
    public final Arguments arguments;
    public final Statement body;
    private final Range range;
    
    public FunctionDefineStatement(String name, Arguments arguments, Statement body, Range range) {
        this.name = name;
        this.arguments = arguments;
        this.body = body;
        this.range = range;
    }

    @Override
    public Range getRange() {
        return range;
    }

    @Override
    public Value eval() {
        ScopeHandler.setFunction(name, new UserDefinedFunction(arguments, body, range));
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
        if (body instanceof ReturnStatement) {
            return String.format("def %s%s = %s", name, arguments, ((ReturnStatement)body).expression);
        }
        return String.format("def %s%s %s", name, arguments, body);
    }
}
