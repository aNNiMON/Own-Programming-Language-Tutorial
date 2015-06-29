package com.annimon.ownlang.parser.ast;

/**
 *
 * @author aNNiMON
 */
public final class ArrayAssignmentStatement implements Statement {
    
    public final ArrayAccessExpression array;
    public final Expression expression;

    public ArrayAssignmentStatement(ArrayAccessExpression array, Expression expression) {
        this.array = array;
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        array.getArray().set(array.lastIndex(), expression.eval());
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s = %s", array, expression);
    }
}
