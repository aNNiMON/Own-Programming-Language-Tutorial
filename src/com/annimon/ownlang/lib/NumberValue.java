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
    public double asNumber() {
        return value;
    }

    @Override
    public String asString() {
        return Double.toString(value);
    }

    @Override
    public String toString() {
        return asString();
    }
}
