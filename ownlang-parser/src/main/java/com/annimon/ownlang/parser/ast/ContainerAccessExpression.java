package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author aNNiMON
 */
public final class ContainerAccessExpression implements Node, Accessible {

    private static final Pattern PATTERN_SIMPLE_INDEX = Pattern.compile("^\"[a-zA-Z$_]\\w*\"");

    public final Node root;
    public final List<Node> indices;
    private final boolean[] simpleIndices;
    private final boolean rootIsVariable;

    public ContainerAccessExpression(String variable, List<Node> indices) {
        this(new VariableExpression(variable), indices);
    }

    public ContainerAccessExpression(Node root, List<Node> indices) {
        rootIsVariable = root instanceof VariableExpression;
        this.root = root;
        this.indices = indices;
        simpleIndices = precomputeSimpleIndices();
    }

    public boolean rootIsVariable() {
        return rootIsVariable;
    }

    public Node getRoot() {
        return root;
    }

    @Override
    public Value eval() {
        return get();
    }
    
    @Override
    public Value get() {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        return switch (container.type()) {
            case Types.ARRAY -> ((ArrayValue) container).get(lastIndex);
            case Types.MAP -> ((MapValue) container).get(lastIndex);
            case Types.STRING -> ((StringValue) container).access(lastIndex);
            case Types.CLASS -> ((ClassInstanceValue) container).access(lastIndex);
            default -> throw new TypeException("Array or map expected. Got " + Types.typeToString(container.type()));
        };
    }

    @Override
    public Value set(Value value) {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case Types.ARRAY -> ((ArrayValue) container).set(lastIndex.asInt(), value);
            case Types.MAP -> ((MapValue) container).set(lastIndex, value);
            case Types.CLASS -> ((ClassInstanceValue) container).set(lastIndex, value);
            default -> throw new TypeException("Array or map expected. Got " + container.type());
        }
        return value;
    }
    
    public Value getContainer() {
        Value container = root.eval();
        final int last = indices.size() - 1;
        for (int i = 0; i < last; i++) {
            final Value index = index(i);
            container = switch (container.type()) {
                case Types.ARRAY -> ((ArrayValue) container).get(index.asInt());
                case Types.MAP -> ((MapValue) container).get(index);
                default -> throw new TypeException("Array or map expected");
            };
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

    private boolean[] precomputeSimpleIndices() {
        final boolean[] result = new boolean[indices.size()];
        int i = 0;
        for (Node index : indices) {
            String indexStr = index.toString();
            result[i] = PATTERN_SIMPLE_INDEX.matcher(indexStr).matches();
            i++;
        }
        return result;
    }

    @Override
    public String toString() {
        final var sb = new StringBuilder(root.toString());
        int i = 0;
        for (Node index : indices) {
            String indexStr = index.toString();
            if (simpleIndices[i]) {
                sb.append('.').append(indexStr, 1, indexStr.length() - 1);
            } else {
                sb.append('[').append(indexStr).append(']');
            }
            i++;
        }
        return sb.toString();
    }
}
