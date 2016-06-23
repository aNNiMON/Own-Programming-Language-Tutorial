package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OperationIsNotSupportedException;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class ConditionalExpression implements Expression {
    
    public static enum Operator {
        EQUALS("=="),
        NOT_EQUALS("!="),
        
        LT("<"),
        LTEQ("<="),
        GT(">"),
        GTEQ(">="),
        
        AND("&&"),
        OR("||");
        
        private final String name;

        private Operator(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    public final Expression expr1, expr2;
    public final Operator operation;

    public ConditionalExpression(Operator operation, Expression expr1, Expression expr2) {
        this.operation = operation;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Value eval() {
        final Value value1 = expr1.eval();
        switch (operation) {
            case AND: return NumberValue.fromBoolean(
                    (value1.asInt() != 0) && (expr2.eval().asInt() != 0) );
            case OR: return NumberValue.fromBoolean(
                    (value1.asInt() != 0) || (expr2.eval().asInt() != 0) );
        }
        
        
        final Value value2 = expr2.eval();
        
        double number1, number2;
        if (value1.type() == Types.NUMBER) {
            number1 = value1.asNumber();
            number2 = value2.asNumber();
        } else {
            number1 = value1.compareTo(value2);
            number2 = 0;
        }
        
        boolean result;
        switch (operation) {
            case EQUALS: result = number1 == number2; break;
            case NOT_EQUALS: result = number1 != number2; break;
            
            case LT: result = number1 < number2; break;
            case LTEQ: result = number1 <= number2; break;
            case GT: result = number1 > number2; break;
            case GTEQ: result = number1 >= number2; break;
            
            default:
                throw new OperationIsNotSupportedException(operation);
        }
        return NumberValue.fromBoolean(result);
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
        return String.format("%s %s %s", expr1, operation.getName(), expr2);
    }
}
