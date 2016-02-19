package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.Console;

/**
 *
 * @author aNNiMON
 */
public final class PrintStatement implements Statement {
    
    public final Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() {
        Console.print(expression.eval());
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "print " + expression;
    }
}
