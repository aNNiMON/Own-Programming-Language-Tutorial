package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OwnLangRuntimeException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.util.Range;
import com.annimon.ownlang.util.SourceLocation;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author aNNiMON
 */
public final class ContainerAccessExpression implements Node, Accessible, SourceLocation {

    private static final Pattern PATTERN_SIMPLE_INDEX = Pattern.compile("^\"[a-zA-Z$_]\\w*\"");

    public final Node root;
    public final List<Node> indices;
    private final boolean[] simpleIndices;
    private final boolean rootIsVariable;
    private final Range range;

    public ContainerAccessExpression(Node root, List<Node> indices, Range range) {
        rootIsVariable = root instanceof VariableExpression;
        this.root = root;
        this.indices = indices;
        this.range = range;
        simpleIndices = precomputeSimpleIndices();
    }

    public boolean rootIsVariable() {
        return rootIsVariable;
    }

    @Override
    public Range getRange() {
        return range;
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
            case Types.ARRAY -> {
                final ArrayValue arr = (ArrayValue) container;
                final int size = arr.size();
                if (lastIndex.type() != Types.NUMBER) {
                    yield arr.get(lastIndex);
                } else {
                    final int index = lastIndex.asInt();
                    if (0 <= index && index < size) {
                        yield arr.get(index);
                    } else {
                        throw outOfBounds(index, size);
                    }
                }
            }
            case Types.MAP -> ((MapValue) container).get(lastIndex);
            case Types.STRING -> ((StringValue) container).access(lastIndex);
            case Types.CLASS -> ((ClassInstance) container).access(lastIndex);
            default -> throw arrayOrMapExpected(container, " while accessing a container");
        };
    }

    @Override
    public Value set(Value value) {
        final Value container = getContainer();
        final Value lastIndex = lastIndex();
        switch (container.type()) {
            case Types.ARRAY -> {
                final ArrayValue arr = (ArrayValue) container;
                final int size = arr.size();
                final int index = lastIndex.asInt();
                if (0 <= index && index < size) {
                    arr.set(index, value);
                } else {
                    throw outOfBounds(index, size);
                }
            }
            case Types.MAP -> ((MapValue) container).set(lastIndex, value);
            case Types.CLASS -> ((ClassInstance) container).set(lastIndex, value);
            default -> throw arrayOrMapExpected(container, " while setting a value to container");
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
                default -> throw arrayOrMapExpected(container, " while resolving a container");
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

    private OwnLangRuntimeException outOfBounds(int index, int size) {
        return new OwnLangRuntimeException(
                "Index %d is out of bounds for array length %d".formatted(index, size), range);
    }

    private TypeException arrayOrMapExpected(Value v, String message) {
        return new TypeException("Array or map expected"
                + (message == null ? "" : message)
                + ". Got " + Types.typeToString(v.type()), range);
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
