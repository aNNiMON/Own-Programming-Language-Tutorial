package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Constants;

/**
 *
 * @author aNNiMON
 */
public final class ConstantExpression implements Expression {
    
    private final String name;
    
    public ConstantExpression(String name) {
        this.name = name;
    }

    @Override
    public double eval() {
        if (!Constants.isExists(name)) throw new RuntimeException("Constant does not exists");
        return Constants.get(name);
    }

    @Override
    public String toString() {
//        return String.format("%s [%f]", name, Constants.get(name));
        return String.format("%s", name);
    }
}
