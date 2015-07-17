package com.annimon.ownlang.lib;

/**
 *
 * @author aNNiMON
 */
public final class FunctionValue implements Value {

    private final Function value;

    public FunctionValue(Function value) {
        this.value = value;
    }
    
    @Override
    public double asNumber() {
        throw new RuntimeException("Cannot cast function to number");
    }

    @Override
    public String asString() {
        return value.toString();
    }

    public Function getValue() {
        return value;
    }

    @Override
    public String toString() {
        return asString();
    }
}
