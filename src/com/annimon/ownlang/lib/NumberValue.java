package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public final class NumberValue implements Value {
    
    public static final NumberValue MINUS_ONE = new NumberValue(-1);
    public static final NumberValue ZERO = new NumberValue(0);
    public static final NumberValue ONE = new NumberValue(1);
    
    public static NumberValue fromBoolean(boolean b) {
        return b ? ONE : ZERO;
    }
    
    private final Number value;
    
    public NumberValue(Number value) {
        this.value = value;
    }
    
    @Override
    public int type() {
        return Types.NUMBER;
    }
    
    @Override
    public Number raw() {
        return value;
    }
    
    public boolean asBoolean() {
        return value.intValue() != 0;
    }
    
    public byte asByte() {
        return value.byteValue();
    }
    
    public short asShort() {
        return value.shortValue();
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    public long asLong() {
        return value.longValue();
    }

    public float asFloat() {
        return value.floatValue();
    }

    public double asDouble() {
        return value.doubleValue();
    }
    
    @Override
    public double asNumber() {
        return value.doubleValue();
    }

    @Override
    public String asString() {
        return value.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + value.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final Number other = ((NumberValue) obj).value;
        if (value instanceof Double || other instanceof Double) {
            return Double.compare(value.doubleValue(), other.doubleValue()) == 0;
        }
        if (value instanceof Float || other instanceof Float) {
            return Float.compare(value.floatValue(), other.floatValue()) == 0;
        }
        if (value instanceof Long || other instanceof Long) {
            return Long.compare(value.longValue(), other.longValue()) == 0;
        }
        return Integer.compare(value.intValue(), other.intValue()) == 0;
    }
    
    @Override
    public int compareTo(Value o) {
        if (o.type() == Types.NUMBER) {
            final Number other = ((NumberValue) o).value;
            if (value instanceof Double || other instanceof Double) {
                return Double.compare(value.doubleValue(), other.doubleValue());
            }
            if (value instanceof Float || other instanceof Float) {
                return Float.compare(value.floatValue(), other.floatValue());
            }
            if (value instanceof Long || other instanceof Long) {
                return Long.compare(value.longValue(), other.longValue());
            }
            return Integer.compare(value.intValue(), other.intValue());
        }
        return asString().compareTo(o.asString());
    }

    @Override
    public String toString() {
        return asString();
    }
}
