package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OperationIsNotSupportedException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class UnaryExpression implements Expression, Statement {

    public static enum Operator {
        INCREMENT_PREFIX("++"),
        DECREMENT_PREFIX("--"),
        INCREMENT_POSTFIX("++"),
        DECREMENT_POSTFIX("--"),
        NEGATE("-"),
        // Boolean
        NOT("!"),
        // Bitwise
        COMPLEMENT("~");
        
        private final String name;

        private Operator(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public final Expression expr1;
    public final Operator operation;

    public UnaryExpression(Operator operation, Expression expr1) {
        this.operation = operation;
        this.expr1 = expr1;
    }
    
    @Override
    public void execute() {
        eval();
    }
    
    @Override
    public Value eval() {
        final Value value = expr1.eval();
        switch (operation) {
            case INCREMENT_PREFIX: {
                if (expr1 instanceof Accessible) {
                    return ((Accessible) expr1).set(new NumberValue(value.asNumber() + 1));
                }
                return new NumberValue(value.asNumber() + 1);
            }
            case DECREMENT_PREFIX: {
                if (expr1 instanceof Accessible) {
                    return ((Accessible) expr1).set(new NumberValue(value.asNumber() - 1));
                }
                return new NumberValue(value.asNumber() - 1);
            }
            case INCREMENT_POSTFIX: {
                if (expr1 instanceof Accessible) {
                    ((Accessible) expr1).set(new NumberValue(value.asNumber() + 1));
                    return value;
                }
                return new NumberValue(value.asNumber() + 1);
            }
            case DECREMENT_POSTFIX: {
                if (expr1 instanceof Accessible) {
                    ((Accessible) expr1).set(new NumberValue(value.asNumber() - 1));
                    return value;
                }
                return new NumberValue(value.asNumber() - 1);
            }
            case NEGATE: return new NumberValue(-value.asNumber());
            case COMPLEMENT: return new NumberValue(~(int)value.asNumber());
            case NOT: return new NumberValue(value.asNumber() != 0 ? 0 : 1);
            default:
                throw new OperationIsNotSupportedException(operation);
        }
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s", operation, expr1);
    }
}
