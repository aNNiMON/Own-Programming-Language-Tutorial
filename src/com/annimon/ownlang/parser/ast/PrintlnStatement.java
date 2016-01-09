package com.annimon.ownlang.parser.ast;

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
        System.out.println(expression.eval());
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "println " + expression;
    }
}
