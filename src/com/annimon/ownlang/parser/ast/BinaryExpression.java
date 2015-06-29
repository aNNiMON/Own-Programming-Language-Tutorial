package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ArrayValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Value;

/**
 *
 * @author aNNiMON
 */
public final class BinaryExpression implements Expression {
    
    public final Expression expr1, expr2;
    public final char operation;

    public BinaryExpression(char operation, Expression expr1, Expression expr2) {
        this.operation = operation;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public Value eval() {
        final Value value1 = expr1.eval();
        final Value value2 = expr2.eval();
        if ( (value1 instanceof StringValue) || (value1 instanceof ArrayValue) ) {
            final String string1 = value1.asString();
            switch (operation) {
                case '*': {
                    final int iterations = (int) value2.asNumber();
                    final StringBuilder buffer = new StringBuilder();
                    for (int i = 0; i < iterations; i++) {
                        buffer.append(string1);
                    }
                    return new StringValue(buffer.toString());
                }
                case '+':
                default:
                    return new StringValue(string1 + value2.asString());
            }
        }
        
        final double number1 = value1.asNumber();
        final double number2 = value2.asNumber();
        switch (operation) {
            case '-': return new NumberValue(number1 - number2);
            case '*': return new NumberValue(number1 * number2);
            case '/': return new NumberValue(number1 / number2);
            case '+':
            default:
                return new NumberValue(number1 + number2);
        }
    }
    
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("[%s %c %s]", expr1, operation, expr2);
    }
}
