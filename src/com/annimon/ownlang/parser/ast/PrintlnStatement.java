package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.Console;

/**
 *
 * @author aNNiMON
 */
public final class PrintlnStatement implements Statement {
    
    public final Expression expression;

    public PrintlnStatement(Expression expression) {
        this.expression = expression;
    }

    @Override
    public void execute() {
        Console.println(expression.eval().asString());
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
        return "println " + expression;
    }
}
