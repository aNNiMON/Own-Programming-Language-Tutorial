package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class ContainerAccessExpression implements Expression {
    
    public final String variable;
    public final List<Expression> indices;

    public ContainerAccessExpression(String variable, List<Expression> indices) {
        this.variable = variable;
        this.indices = indices;
    }
    
    @Override
    public Value eval() {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case Types.ARRAY:
                final int arrayIndex = (int) lastIndex.asNumber();
                return ((ArrayValue) container).get(arrayIndex);

            case Types.MAP:
                return ((MapValue) container).get(lastIndex);
                
            default:
                throw new TypeException("Array or map expected");
        }
    }
    
    public Value getContainer() {
        Value container = Variables.get(variable);
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            final Value index = index(i);
            switch (container.type()) {
                case Types.ARRAY:
                    final int arrayIndex = (int) index.asNumber();
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
    
    private ArrayValue consumeArray(Value value) {
        if (value.type() != Types.ARRAY) {
            throw new TypeException("Array expected");
        }
        return (ArrayValue) value;
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
    public String toString() {
        return variable + indices;
    }
}
