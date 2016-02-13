package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.MapValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class ContainerAssignmentStatement implements Statement {
    
    public final ContainerAccessExpression containerExpr;
    public final Expression expression;

    public ContainerAssignmentStatement(ContainerAccessExpression array, Expression expression) {
        this.containerExpr = array;
        this.expression = expression;
    }
    
    @Override
    public void execute() {
        final Value container = containerExpr.getContainer();
        final Value lastIndex = containerExpr.lastIndex();
        switch (container.type()) {
            case Types.ARRAY:
                final int arrayIndex = (int) lastIndex.asNumber();
                ((ArrayValue) container).set(arrayIndex, expression.eval());
                return;

            case Types.MAP:
                ((MapValue) container).set(lastIndex, expression.eval());
                return;
                
            default:
                throw new TypeException("Array or map expected. Got " + container.type());
        }
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s = %s", containerExpr, expression);
    }
}
