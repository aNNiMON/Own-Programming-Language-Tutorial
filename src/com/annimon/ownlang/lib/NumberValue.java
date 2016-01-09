package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public final class NumberValue implements Value {
    
    public static final NumberValue ZERO = new NumberValue(0);
    public static final NumberValue ONE = new NumberValue(1);
    
    public static NumberValue fromBoolean(boolean b) {
        return b ? ONE : ZERO;
    }
    
    private final double value;
    
    public NumberValue(double value) {
        this.value = value;
    }
    
    @Override
    public int type() {
        return Types.NUMBER;
    }
    
    @Override
    public double asNumber() {
        return value;
    }

    @Override
    public String asString() {
        return Double.toString(value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final NumberValue other = (NumberValue) obj;
        return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
    }
    
    

    @Override
    public String toString() {
        return asString();
    }
}
