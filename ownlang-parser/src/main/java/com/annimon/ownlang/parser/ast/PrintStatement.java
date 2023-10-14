package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class PrintStatement extends InterruptableNode implements Statement {
    
    public final Node expression;

    public PrintStatement(Node expression) {
        this.expression = expression;
    }

    @Override
    public Value eval() {
        super.interruptionCheck();
        Console.print(expression.eval().asString());
        return NumberValue.ZERO;
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <R, T> R accept(ResultVisitor<R, T> visitor, T t) {
        return visitor.visit(this, t);
    }

    @Override
    public String toString() {
        return "print " + expression;
    }
}
