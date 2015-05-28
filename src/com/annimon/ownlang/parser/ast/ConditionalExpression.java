package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class ConditionalExpression implements Expression {
    
    private final Expression expr1, expr2;
    private final char operation;

    public ConditionalExpression(char operation, Expression expr1, Expression expr2) {
        this.operation = operation;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Value eval() {
        final Value value1 = expr1.eval();
        final Value value2 = expr2.eval();
        if (value1 instanceof StringValue) {
            final String string1 = value1.asString();
            final String string2 = value2.asString();
            switch (operation) {
                case '<': return new NumberValue(string1.compareTo(string2) < 0);
                case '>': return new NumberValue(string1.compareTo(string2) > 0);
                case '=': 
                default:
                    return new NumberValue(string1.equals(string2));
            }
        }
        
        final double number1 = value1.asNumber();
        final double number2 = value2.asNumber();
        switch (operation) {
            case '<': return new NumberValue(number1 < number2);
            case '>': return new NumberValue(number1 > number2);
            case '=': 
            default:
                return new NumberValue(number1 == number2);
        }
    }

    @Override
    public String toString() {
        return String.format("[%s %c %s]", expr1, operation, expr2);
    }
}
