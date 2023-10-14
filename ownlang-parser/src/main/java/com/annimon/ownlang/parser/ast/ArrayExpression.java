package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Value;
import java.util.List;

/**
 *
 * @author aNNiMON
 */
public final class ArrayExpression implements Node {
    
    public final List<Node> elements;

    public ArrayExpression(List<Node> arguments) {
        this.elements = arguments;
    }
    
    @Override
    public Value eval() {
        final int size = elements.size();
        final ArrayValue array = new ArrayValue(size);
        for (int i = 0; i < size; i++) {
            array.set(i, elements.get(i).eval());
        }
        return array;
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
        return elements.toString();
    }
}
