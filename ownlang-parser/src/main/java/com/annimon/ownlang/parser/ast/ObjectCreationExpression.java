package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.UnknownClassException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;
import java.util.Iterator;
import java.util.List;

public final class ObjectCreationExpression implements Node, SourceLocation {
    
    public final String className;
    public final List<Node> constructorArguments;
    private Range range;

    public ObjectCreationExpression(String className, List<Node> constructorArguments) {
        this.className = className;
        this.constructorArguments = constructorArguments;
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
        final ClassDeclaration cd = ScopeHandler.getClassDeclaration(className);
        if (cd != null) {
            return cd.newInstance(constructorArgs());
        }

        // Is Instantiable?
        if (ScopeHandler.isVariableOrConstantExists(className)) {
            final Value variable = ScopeHandler.getVariableOrConstant(className);
            if (variable instanceof Instantiable instantiable) {
                return instantiable.newInstance(constructorArgs());
            }
        }
        throw new UnknownClassException(className, range);
    }

    private Value[] constructorArgs() {
        final int argsSize = constructorArguments.size();
        final Value[] args = new Value[argsSize];
        int i = 0;
        for (Node argument : constructorArguments) {
            args[i++] = argument.eval();
        }
        return args;
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
        final StringBuilder sb = new StringBuilder();
        sb.append("new ").append(className).append(' ');
        final Iterator<Node> it = constructorArguments.iterator();
        if (it.hasNext()) {
            sb.append(it.next());
            while (it.hasNext()) {
                sb.append(", ").append(it.next());
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
