package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.StringValue;
import com.annimon.ownlang.lib.Types;
import com.annimon.ownlang.lib.Value;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Helper for build and test AST nodes.
 * @author aNNiMON
 */
public final class ASTHelper {
    
    public static void assertValue(NumberValue expected, Value actual) {
        assertEquals(Types.NUMBER, actual.type());
        if (expected.raw() instanceof Double) {
            assertEquals(expected.asNumber(), actual.asNumber(), 0.001);
        }
        assertEquals(expected.asInt(), actual.asInt());
    }
    
    public static void assertValue(StringValue expected, Value actual) {
        assertEquals(Types.STRING, actual.type());
        assertEquals(expected.asString(), actual.asString());
    }
    
    
    public static BlockStatement block(Statement... statements) {
        final BlockStatement result = new BlockStatement();
        for (Statement statement : statements) {
            result.add(statement);
        }
        return result;
    }
    
    public static AssignmentExpression assign(String variable, Node expr) {
        return assign(var(variable), expr);
    }
    
    public static AssignmentExpression assign(Accessible accessible, Node expr) {
        return assign(null, accessible, expr);
    }
    
    public static AssignmentExpression assign(BinaryExpression.Operator op, Accessible accessible, Node expr) {
        return new AssignmentExpression(op, accessible, expr, null);
    }
    
    public static BinaryExpression operator(BinaryExpression.Operator op, Node left, Node right) {
        return new BinaryExpression(op, left, right);
    }
    
    public static ValueExpression value(Number value) {
        return new ValueExpression(value);
    }
    
    public static ValueExpression value(String value) {
        return new ValueExpression(value);
    }
    
    public static VariableExpression var(String value) {
        return new VariableExpression(value);
    }
    
    
    public static NumberValue number(Number value) {
        return NumberValue.of(value);
    }
    
    public static StringValue string(String value) {
        return new StringValue(value);
    }
}
