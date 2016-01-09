package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;
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
            return getArray().get(lastIndex());
        }
        return consumeMap(container).get(indices.get(0).eval());
    }
    
    public ArrayValue getArray() {
        ArrayValue array = consumeArray(Variables.get(variable));
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            array = consumeArray( array.get(index(i)) );
        }
        return array;
    }
    
    public int lastIndex() {
        return index(indices.size() - 1);
    }
    
    private int index(int index) {
        return (int) indices.get(index).eval().asNumber();
    }
    
    private ArrayValue consumeArray(Value value) {
        if (value.type() != Types.ARRAY) {
            throw new RuntimeException("Array expected");
        }
        return (ArrayValue) value;
    }
    
    public MapValue consumeMap(Value value) {
        if (value.type() != Types.MAP) {
            throw new RuntimeException("Map expected");
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
