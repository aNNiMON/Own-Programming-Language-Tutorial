package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.lib.Variables;

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
        final Value container = Variables.get(array.variable);
        if (container instanceof ArrayValue) {
            array.getArray().set(array.lastIndex(), expression.eval());
            return;
        }
        array.consumeMap(container).set(array.indices.get(0).eval(), expression.eval());
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
