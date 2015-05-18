package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public final class NumberExpression implements Expression {
    
    private final double value;
    
    public NumberExpression(double value) {
        this.value = value;
    }

    @Override
    public double eval() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
