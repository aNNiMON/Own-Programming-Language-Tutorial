package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.Variables;

/**
 *
 * @author aNNiMON
 */
public final class VariabletExpression implements Expression {
    
    private final String name;
    
    public VariabletExpression(String name) {
        this.name = name;
    }

    @Override
    public double eval() {
        if (!Variables.isExists(name)) throw new RuntimeException("Constant does not exists");
        return Variables.get(name);
    }

    @Override
    public String toString() {
//        return String.format("%s [%f]", name, Constants.get(name));
        return String.format("%s", name);
    }
}
