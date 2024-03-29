package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Value;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class MapExpression implements Node {
    
    public final Map<Node, Node> elements;

    public MapExpression(Map<Node, Node> arguments) {
        this.elements = arguments;
    }
    
    @Override
    public Value eval() {
        final int size = elements.size();
        final MapValue map = new MapValue(size);
        for (Map.Entry<Node, Node> entry : elements.entrySet()) {
            map.set(entry.getKey().eval(), entry.getValue().eval());
        }
        return map;
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
        sb.append('{');
        Iterator<Map.Entry<Node, Node>> it = elements.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<Node, Node> entry = it.next();
            sb.append(entry.getKey()).append(" : ").append(entry.getValue());
            while (it.hasNext()) {
                entry = it.next();
                sb.append(", ");
                sb.append(entry.getKey()).append(" : ").append(entry.getValue());
            }
        }
        sb.append('}');
        return sb.toString();
    }
}
