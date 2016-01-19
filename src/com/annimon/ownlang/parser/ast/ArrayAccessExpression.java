package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class ArrayAccessExpression implements Expression {
    
    public final String variable;
    public final List<Expression> indices;

    public ArrayAccessExpression(String variable, List<Expression> indices) {
        this.variable = variable;
        this.indices = indices;
    }
    
    @Override
    public Value eval() {
        Value container = Variables.get(variable);
        if (container.type() == Types.ARRAY) {
            final int lastIndex = (int) lastIndex().asNumber();
            return getArray().get(lastIndex);
        }
        return getMap().get(lastIndex());
    }
    
    public ArrayValue getArray() {
        ArrayValue array = consumeArray(Variables.get(variable));
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            final int index = (int) index(i).asNumber();
            array = consumeArray( array.get(index) );
        }
        return array;
    }
    
    public MapValue getMap() {
        MapValue map = consumeMap(Variables.get(variable));
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            map = consumeMap( map.get(index(i)) );
        }
        return map;
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
