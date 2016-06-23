package com.annimon.ownlang.parser.ast;

/**
 * Wrapper for expressions, which can be used as statements.
 * 
 * @author aNNiMON
 */
public final class ExprStatement implements Statement {
    
    public final Expression expr;
    
    public ExprStatement(Expression function) {
        this.expr = function;
    }

    @Override
    public void execute() {
        expr.eval();
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
        return expr.toString();
    }
}
