package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public final class NumberValue implements Value {
    
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
