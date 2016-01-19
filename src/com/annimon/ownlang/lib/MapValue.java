package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.TypeException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author aNNiMON
 */
public class MapValue implements Value, Iterable<Map.Entry<Value, Value>> {
    
    public static final MapValue EMPTY = new MapValue(1);
    
    private final Map<Value, Value> map;

    public MapValue(int size) {
        this.map = new HashMap<>(size);
    }

    public MapValue(Map<Value, Value> map) {
        this.map = map;
    }
    
    @Override
    public int type() {
        return Types.MAP;
    }
    
    public int size() {
        return map.size();
    }
    
    public boolean containsKey(Value key) {
        return map.containsKey(key);
    }

    public Value get(Value key) {
        return map.get(key);
    }
    
    public void set(Value key, Value value) {
        map.put(key, value);
    }

    @Override
    public double asNumber() {
        throw new TypeException("Cannot cast map to number");
    }

    @Override
    public String asString() {
        return map.toString();
    }

    @Override
    public Iterator<Map.Entry<Value, Value>> iterator() {
        return map.entrySet().iterator();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.map);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final MapValue other = (MapValue) obj;
        return Objects.equals(this.map, other.map);
    }
    
    @Override
    public int compareTo(Value o) {
        if (o.type() == Types.MAP) {
            final int lengthCompare = Integer.compare(size(), ((MapValue) o).size());
            if (lengthCompare != 0) return lengthCompare;
        }
        return asString().compareTo(o.asString());
    }
    
    @Override
    public String toString() {
        return asString();
    }
}
