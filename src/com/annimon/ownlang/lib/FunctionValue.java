package com.annimon.ownlang.lib;

import com.annimon.ownlang.exceptions.TypeException;
import java.util.Objects;

/**
 *
 * @author aNNiMON
 */
public final class FunctionValue implements Value {
    
    public static final FunctionValue EMPTY = new FunctionValue(args -> NumberValue.ZERO);

    private final Function value;

    public FunctionValue(Function value) {
        this.value = value;
    }
    
    @Override
    public int type() {
        return Types.FUNCTION;
    }
    
    @Override
    public double asNumber() {
        throw new TypeException("Cannot cast function to number");
    }

    @Override
    public String asString() {
        return value.toString();
    }

    public Function getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass())
            return false;
        final FunctionValue other = (FunctionValue) obj;
        return Objects.equals(this.value, other.value);
    }
    
    @Override
    public String toString() {
        return asString();
    }
}
