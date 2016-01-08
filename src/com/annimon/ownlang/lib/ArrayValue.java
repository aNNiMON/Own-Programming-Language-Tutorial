package com.annimon.ownlang.lib;

import java.util.Arrays;
import java.util.Iterator;

/**
 *
 * @author aNNiMON
 */
public final class ArrayValue implements Value, Iterable<Value> {
    
    private final Value[] elements;

    public ArrayValue(int size) {
        this.elements = new Value[size];
    }

    public ArrayValue(Value[] elements) {
        this.elements = new Value[elements.length];
        System.arraycopy(elements, 0, this.elements, 0, elements.length);
    }
    
    public ArrayValue(ArrayValue array) {
        this(array.elements);
    }
    
    public Value get(int index) {
        return elements[index];
    }
    
    public void set(int index, Value value) {
        elements[index] = value;
    }
    
    @Override
    public double asNumber() {
        throw new RuntimeException("Cannot cast array to number");
    }

    @Override
    public String asString() {
        return Arrays.toString(elements);
    }

    @Override
    public Iterator<Value> iterator() {
        return Arrays.asList(elements).iterator();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Arrays.deepHashCode(this.elements);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final ArrayValue other = (ArrayValue) obj;
        return Arrays.deepEquals(this.elements, other.elements);
    }

    @Override
    public String toString() {
        return asString();
    }
}
