package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.exceptions.OperationIsNotSupportedException;
import com.annimon.ownlang.exceptions.TypeException;
import com.annimon.ownlang.lib.*;

/**
 *
 * @author aNNiMON
 */
public final class BinaryExpression implements Expression {
    
    public enum Operator {
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
        URSHIFT(">>>"),

        // Addition operators for future usage or overloading
        AT("@"),
        CARETCARET("^^"),
        RANGE(".."),
        POWER("**"),
        ELVIS("?:");
        
        private final String name;

        Operator(String name) {
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
        try {
            return eval(value1, value2);
        } catch (OperationIsNotSupportedException ex) {
            if (ScopeHandler.isFunctionExists(operation.toString())) {
                return ScopeHandler.getFunction(operation.toString()).execute(value1, value2);
            }
            throw ex;
        }
    }
    
    private Value eval(Value value1, Value value2) {
        return switch (operation) {
            case ADD -> add(value1, value2);
            case SUBTRACT -> subtract(value1, value2);
            case MULTIPLY -> multiply(value1, value2);
            case DIVIDE -> divide(value1, value2);
            case REMAINDER -> remainder(value1, value2);
            case PUSH -> push(value1, value2);
            case AND -> and(value1, value2);
            case OR -> or(value1, value2);
            case XOR -> xor(value1, value2);
            case LSHIFT -> lshift(value1, value2);
            case RSHIFT -> rshift(value1, value2);
            case URSHIFT -> urshift(value1, value2);
            default -> throw new OperationIsNotSupportedException(operation);
        };
    }
    
    private Value add(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return add((NumberValue) value1, value2);
            case Types.ARRAY: return ArrayValue.add((ArrayValue) value1, value2);
            case Types.MAP:
                if (value2.type() != Types.MAP)
                    throw new TypeException("Cannot merge non map value to map");
                return MapValue.merge((MapValue) value1, (MapValue) value2);
            case Types.FUNCTION: /* TODO: combining functions */
            case Types.STRING:
            default:
                // Concatenation strings
                return new StringValue(value1.asString() + value2.asString());
        }
    }
    
    private Value add(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 + number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Double || number2 instanceof Double) {
                return NumberValue.of(number1.doubleValue() + number2.doubleValue());
            }
            if (number1 instanceof Float || number2 instanceof Float) {
                return NumberValue.of(number1.floatValue() + number2.floatValue());
            }
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() + number2.longValue());
            }
            return NumberValue.of(number1.intValue() + number2.intValue());
        }
        // number1 + other
        if (number1 instanceof Double) {
            return NumberValue.of(number1.doubleValue() + value2.asNumber());
        }
        if (number1 instanceof Float) {
            return NumberValue.of(number1.floatValue() + value2.asNumber());
        }
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() + value2.asInt());
        }
        return NumberValue.of(number1.intValue() + value2.asInt());
    }
    
    private Value subtract(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return subtract((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value subtract(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 - number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Double || number2 instanceof Double) {
                return NumberValue.of(number1.doubleValue() - number2.doubleValue());
            }
            if (number1 instanceof Float || number2 instanceof Float) {
                return NumberValue.of(number1.floatValue() - number2.floatValue());
            }
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() - number2.longValue());
            }
            return NumberValue.of(number1.intValue() - number2.intValue());
        }
        // number1 - other
        if (number1 instanceof Double) {
            return NumberValue.of(number1.doubleValue() - value2.asNumber());
        }
        if (number1 instanceof Float) {
            return NumberValue.of(number1.floatValue() - value2.asNumber());
        }
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() - value2.asInt());
        }
        return NumberValue.of(number1.intValue() - value2.asInt());
    }
    
    private Value multiply(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return multiply((NumberValue) value1, value2);
            case Types.STRING: return multiply((StringValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value multiply(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 * number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Double || number2 instanceof Double) {
                return NumberValue.of(number1.doubleValue() * number2.doubleValue());
            }
            if (number1 instanceof Float || number2 instanceof Float) {
                return NumberValue.of(number1.floatValue() * number2.floatValue());
            }
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() * number2.longValue());
            }
            return NumberValue.of(number1.intValue() * number2.intValue());
        }
        // number1 * other
        if (number1 instanceof Double) {
            return NumberValue.of(number1.doubleValue() * value2.asNumber());
        }
        if (number1 instanceof Float) {
            return NumberValue.of(number1.floatValue() * value2.asNumber());
        }
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() * value2.asInt());
        }
        return NumberValue.of(number1.intValue() * value2.asInt());
    }

    private Value multiply(StringValue value1, Value value2) {
        final String string1 = value1.asString();
        final int iterations = value2.asInt();
        return new StringValue(String.valueOf(string1).repeat(Math.max(0, iterations)));
    }
    
    private Value divide(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return divide((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value divide(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 / number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Double || number2 instanceof Double) {
                return NumberValue.of(number1.doubleValue() / number2.doubleValue());
            }
            if (number1 instanceof Float || number2 instanceof Float) {
                return NumberValue.of(number1.floatValue() / number2.floatValue());
            }
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() / number2.longValue());
            }
            return NumberValue.of(number1.intValue() / number2.intValue());
        }
        // number1 / other
        if (number1 instanceof Double) {
            return NumberValue.of(number1.doubleValue() / value2.asNumber());
        }
        if (number1 instanceof Float) {
            return NumberValue.of(number1.floatValue() / value2.asNumber());
        }
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() / value2.asInt());
        }
        return NumberValue.of(number1.intValue() / value2.asInt());
    }
    
    private Value remainder(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return remainder((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value remainder(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 % number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Double || number2 instanceof Double) {
                return NumberValue.of(number1.doubleValue() % number2.doubleValue());
            }
            if (number1 instanceof Float || number2 instanceof Float) {
                return NumberValue.of(number1.floatValue() % number2.floatValue());
            }
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() % number2.longValue());
            }
            return NumberValue.of(number1.intValue() % number2.intValue());
        }
        // number1 % other
        if (number1 instanceof Double) {
            return NumberValue.of(number1.doubleValue() % value2.asNumber());
        }
        if (number1 instanceof Float) {
            return NumberValue.of(number1.floatValue() % value2.asNumber());
        }
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() % value2.asInt());
        }
        return NumberValue.of(number1.intValue() % value2.asInt());
    }
    
    private Value push(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.ARRAY: return ArrayValue.add((ArrayValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }

    private Value and(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return and((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value and(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 & number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() & number2.longValue());
            }
            return NumberValue.of(number1.intValue() & number2.intValue());
        }
        // number1 & other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() & value2.asInt());
        }
        return NumberValue.of(number1.intValue() & value2.asInt());
    }
    
    private Value or(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return or((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value or(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 | number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() | number2.longValue());
            }
            return NumberValue.of(number1.intValue() | number2.intValue());
        }
        // number1 | other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() | value2.asInt());
        }
        return NumberValue.of(number1.intValue() | value2.asInt());
    }
    
    private Value xor(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return xor((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value xor(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 ^ number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() ^ number2.longValue());
            }
            return NumberValue.of(number1.intValue() ^ number2.intValue());
        }
        // number1 ^ other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() ^ value2.asInt());
        }
        return NumberValue.of(number1.intValue() ^ value2.asInt());
    }
    
    private Value lshift(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return lshift((NumberValue) value1, value2);
            case Types.ARRAY: return lshift((ArrayValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }

    private Value lshift(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 << number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() << number2.longValue());
            }
            return NumberValue.of(number1.intValue() << number2.intValue());
        }
        // number1 << other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() << value2.asInt());
        }
        return NumberValue.of(number1.intValue() << value2.asInt());
    }

    private Value lshift(ArrayValue value1, Value value2) {
        if (value2.type() != Types.ARRAY)
            throw new TypeException("Cannot merge non array value to array");
        return ArrayValue.merge(value1, (ArrayValue) value2);
    }
    
    private Value rshift(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return rshift((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value rshift(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 >> number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() >> number2.longValue());
            }
            return NumberValue.of(number1.intValue() >> number2.intValue());
        }
        // number1 >> other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() >> value2.asInt());
        }
        return NumberValue.of(number1.intValue() >> value2.asInt());
    }
    
    private Value urshift(Value value1, Value value2) {
        switch (value1.type()) {
            case Types.NUMBER: return urshift((NumberValue) value1, value2);
            default:
                throw new OperationIsNotSupportedException(operation,
                        "for " + Types.typeToString(value1.type()));
        }
    }
    
    private Value urshift(NumberValue value1, Value value2) {
        final Number number1 = value1.raw();
        if (value2.type() == Types.NUMBER) {
            // number1 >>> number2
            final Number number2 = (Number) value2.raw();
            if (number1 instanceof Long || number2 instanceof Long) {
                return NumberValue.of(number1.longValue() >>> number2.longValue());
            }
            return NumberValue.of(number1.intValue() >>> number2.intValue());
        }
        // number1 >>> other
        if (number1 instanceof Long) {
            return NumberValue.of(number1.longValue() >>> value2.asInt());
        }
        return NumberValue.of(number1.intValue() >>> value2.asInt());
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
        return String.format("%s %s %s", expr1, operation, expr2);
    }
}
