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
public final class ContainerAssignmentExpression implements Expression {
    
    public final ContainerAccessExpression containerExpr;
    public final Expression expression;

    public ContainerAssignmentExpression(ContainerAccessExpression array, Expression expression) {
        this.containerExpr = array;
        this.expression = expression;
    }
    
    @Override
    public Value eval() {
        final Value container = containerExpr.getContainer();
        final Value lastIndex = containerExpr.lastIndex();
        switch (container.type()) {
            case Types.ARRAY: {
                final Value result = expression.eval();
                final int arrayIndex = (int) lastIndex.asNumber();
                ((ArrayValue) container).set(arrayIndex, result);
                return result;
            }

            case Types.MAP: {
                final Value result = expression.eval();
                ((MapValue) container).set(lastIndex, result);
                return result;
            }
                
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
