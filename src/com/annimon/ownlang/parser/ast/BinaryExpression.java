package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class BinaryExpression implements Expression {
    
    public static enum Operator {
        ADD("+"),
        SUBTRACT("-"),
        MULTIPLY("*"),
        DIVIDE("/"),
        REMAINDER("%"),
        PUSH("::"),
        // Bitwise
        AND("&"),
        OR("|"),
        XOR("^"),
        LSHIFT("<<"),
        RSHIFT(">>"),
        URSHIFT(">>>");
        
        private final String name;

        private Operator(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    public final Operator operation;
    public final Expression expr1, expr2;

    public BinaryExpression(Operator operation, Expression expr1, Expression expr2) {
        this.operation = operation;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Value eval() {
        final Value value1 = expr1.eval();
        final Value value2 = expr2.eval();
        
        switch (value1.type()) {
            case Types.STRING:
                return eval((StringValue) value1, value2);
            case Types.ARRAY:
                return eval((ArrayValue) value1, value2);
            case Types.NUMBER:
            default:
                return eval(value1, value2);
        }
    }
    
    private Value eval(StringValue value1, Value value2) {
        final String string1 = value1.asString();
        switch (operation) {
            case MULTIPLY: {
                final int iterations = (int) value2.asNumber();
                final StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < iterations; i++) {
                    buffer.append(string1);
                }
                return new StringValue(buffer.toString());
            }
            case ADD:
            default:
                return new StringValue(string1 + value2.asString());
        }
    }
    
    private Value eval(ArrayValue value1, Value value2) {
        switch (operation) {
            case LSHIFT:
                if (value2.type() != Types.ARRAY)
                    throw new RuntimeException("Cannot merge non array value to array");
                return ArrayValue.merge(value1, (ArrayValue) value2);
            case PUSH: 
            default:
                return ArrayValue.add(value1, value2);
        }
    }
    
    private Value eval(Value value1, Value value2) {
        final double number1 = value1.asNumber();
        final double number2 = value2.asNumber();
        double result;
        switch (operation) {
            case ADD: result = number1 + number2; break;
            case SUBTRACT: result = number1 - number2; break;
            case MULTIPLY: result = number1 * number2; break;
            case DIVIDE: result = number1 / number2; break;
            case REMAINDER: result = number1 % number2; break;
            
            // Bitwise
            case AND: result = (int)number1 & (int)number2; break;
            case XOR: result = (int)number1 ^ (int)number2; break;
            case OR: result = (int)number1 | (int)number2; break;
            case LSHIFT: result = (int)number1 << (int)number2; break;
            case RSHIFT: result = (int)number1 >> (int)number2; break;
            case URSHIFT: result = (int)number1 >>> (int)number2; break;
            
            default:
                throw new RuntimeException("Operation " + operation + " is not supported");
        }
        return new NumberValue(result);
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", expr1, operation, expr2);
    }
}
