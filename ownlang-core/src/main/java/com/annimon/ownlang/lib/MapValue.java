package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.TypeException;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author aNNiMON
 */
public class MapValue implements Value, Iterable<Map.Entry<Value, Value>> {
    
    public static final MapValue EMPTY = new MapValue(1);
    
    public static MapValue merge(MapValue map1, MapValue map2) {
        final MapValue result = new MapValue(map1.size() + map2.size());
        result.map.putAll(map1.map);
        result.map.putAll(map2.map);
        return result;
    }

    private final Map<Value, Value> map;

    public MapValue(int size) {
        this.map = new LinkedHashMap<>(size);
    }

    public MapValue(Map<Value, Value> map) {
        this.map = map;
    }

    public boolean ifPresent(String key, Consumer<Value> consumer) {
        return ifPresent(new StringValue(key), consumer);
    }

    public boolean ifPresent(Value key, Consumer<Value> consumer) {
        if (map.containsKey(key)) {
            consumer.accept(map.get(key));
            return true;
        }
        return false;
    }

    public ArrayValue toPairs() {
        final int size = map.size();
        final ArrayValue result = new ArrayValue(size);
        int index = 0;
        for (Map.Entry<Value, Value> entry : map.entrySet()) {
            result.set(index++, new ArrayValue(new Value[] {
                entry.getKey(), entry.getValue()
            }));
        }
        return result;
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

    public void set(String key, Value value) {
        set(new StringValue(key), value);
    }

    public void set(String key, Function function) {
        set(new StringValue(key), new FunctionValue(function));
    }
    
    public void set(Value key, Value value) {
        map.put(key, value);
    }

    public Map<Value, Value> getMap() {
        return map;
    }

    public Map<String, Value> getMapStringKeys() {
        return convertMap(Value::asString, java.util.function.Function.identity());
    }

    public <K, V> Map<K, V> convertMap(java.util.function.Function<? super Value, ? extends K> keyMapper,
                                       java.util.function.Function<? super Value, ? extends V> valueMapper) {
        final Map<K, V> result = new LinkedHashMap<>(map.size());
        map.forEach((key, value) -> result.put(keyMapper.apply(key), valueMapper.apply(value)));
        return result;
    }
    
    @Override
    public Object raw() {
        return map;
    }

    @Override
    public Object asJavaObject() {
        Map<Object, Object> result = new HashMap<>(map.size());
        map.forEach((k, v) -> result.put(k.asJavaObject(), v.asJavaObject()));
        return result;
    }
    
    @Override
    public int asInt() {
        throw new TypeException("Cannot cast map to integer");
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
