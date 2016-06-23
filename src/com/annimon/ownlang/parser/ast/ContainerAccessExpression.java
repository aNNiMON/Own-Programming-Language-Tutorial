package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class ContainerAccessExpression implements Expression, Accessible {
    
    public final String variable;
    public final List<Expression> indices;

    public ContainerAccessExpression(String variable, List<Expression> indices) {
        this.variable = variable;
        this.indices = indices;
    }
    
    @Override
    public Value eval() {
        return get();
    }
    
    @Override
    public Value get() {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case Types.ARRAY:
                final int arrayIndex = lastIndex.asInt();
                return ((ArrayValue) container).get(arrayIndex);

            case Types.MAP:
                return ((MapValue) container).get(lastIndex);
                
            default:
                throw new TypeException("Array or map expected. Got " + container.type());
        }
    }

    @Override
    public Value set(Value value) {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case Types.ARRAY:
                final int arrayIndex = lastIndex.asInt();
                ((ArrayValue) container).set(arrayIndex, value);
                return value;

            case Types.MAP:
                ((MapValue) container).set(lastIndex, value);
                return value;
                
            default:
                throw new TypeException("Array or map expected. Got " + container.type());
        }
    }
    
    public Value getContainer() {
        Value container = Variables.get(variable);
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            final Value index = index(i);
            switch (container.type()) {
                case Types.ARRAY:
                    final int arrayIndex = index.asInt();
                    container = ((ArrayValue) container).get(arrayIndex);
                    break;
                    
                case Types.MAP:
                    container = ((MapValue) container).get(index);
                    break;
                    
                default:
                    throw new TypeException("Array or map expected");
            }
        }
        return container;
    }
    
    public Value lastIndex() {
        return index(indices.size() - 1);
    }
    
    private Value index(int index) {
        return indices.get(index).eval();
    }
    
    public MapValue consumeMap(Value value) {
        if (value.type() != Types.MAP) {
            throw new TypeException("Map expected");
        }
        return (MapValue) value;
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
        return variable + indices;
    }
}
