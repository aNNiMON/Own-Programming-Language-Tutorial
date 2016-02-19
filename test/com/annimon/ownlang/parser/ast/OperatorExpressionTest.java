package com.annimon.ownlang.parser.ast;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;
import static com.annimon.ownlang.parser.ast.BinaryExpression.Operator.*;
import org.junit.Test;

/**
 * @author aNNiMON
 */
public class OperatorExpressionTest {
    
    @Test
    public void testAddition() {
        assertValue(number(4), operator(ADD, value(2), value(2)).eval());
        assertValue(number(6), operator(ADD, value(1), operator(ADD, value(2), value(3))).eval());
        assertValue(string("ABCD"), operator(ADD, value("AB"), value("CD")).eval());
        assertValue(string("AB12"), operator(ADD, value("AB"), operator(ADD, value(10), value(2))).eval());
    }
    
    @Test
    public void testSubtraction() {
        assertValue(number(0), operator(SUBTRACT, value(2), value(2)).eval());
        assertValue(number(110), operator(SUBTRACT, value(100), operator(SUBTRACT, value(20), value(30))).eval());
    }
    
    @Test
    public void testMultiplication() {
        assertValue(number(4), operator(MULTIPLY, value(2), value(2)).eval());
        assertValue(number(30), operator(MULTIPLY, value(5), operator(MULTIPLY, value(-2), value(-3))).eval());
        assertValue(string("ABABAB"), operator(MULTIPLY, value("AB"), value(3)).eval());
    }
    
    @Test
    public void testDivision() {
        assertValue(number(3), operator(DIVIDE, value(6), value(2)).eval());
        assertValue(number(30), operator(DIVIDE, value(-900), operator(DIVIDE, value(60), value(-2))).eval());
    }
    
    @Test()
    public void testDivisionZero() {
        assertValue(number(Double.POSITIVE_INFINITY), operator(DIVIDE, value(2.0), value(0.0)).eval());
    }
    
    @Test(expected = RuntimeException.class)
    public void testDivisionZeroOnIntegers() {
        operator(DIVIDE, value(2), value(0)).eval();
    }
    
    @Test
    public void testRemainder() {
        assertValue(number(2), operator(REMAINDER, value(10), value(4)).eval());
        assertValue(number(5), operator(REMAINDER, value(15), operator(REMAINDER, value(40), value(30))).eval());
    }
    
    @Test()
    public void testRemainderZero() {
        assertValue(number(Double.NaN), operator(REMAINDER, value(2.0), value(0.0)).eval());
    }
    
    @Test(expected = RuntimeException.class)
    public void testRemainderZeroOnIntegers() {
        operator(REMAINDER, value(2), value(0)).eval();
    }
    
    @Test
    public void testAND() {
        assertValue(number(0x04), operator(AND, value(0x04), value(0x0F)).eval());
        assertValue(number(0x00), operator(AND, value(0x04), value(0x08)).eval());
        assertValue(number(8), operator(AND, value(12), value(9)).eval());
    }
    
    @Test
    public void testOR() {
        assertValue(number(12), operator(OR, value(4), value(8)).eval());
        assertValue(number(0x0F), operator(OR, value(3), value(12)).eval());
        assertValue(number(0x0E), operator(OR, value(10), value(4)).eval());
    }
}
