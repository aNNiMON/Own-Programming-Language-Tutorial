package com.annimon.ownlang.parser;

import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.lib.Value;
import com.annimon.ownlang.parser.ast.*;
import org.junit.jupiter.api.Test;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author aNNiMON
 */
public class ParserTest {
    
    @Test
    public void testParsePrimary() {
        assertEval(number(2), "2", value(2));
        assertEval(string("test"), "\"test\"", value("test"));
    }
    
    @Test
    public void testParseAdditive() {
        assertEval( number(5), "2 + 3", operator(BinaryExpression.Operator.ADD, value(2), value(3)) );
        assertEval( number(-1), "2 - 3", operator(BinaryExpression.Operator.SUBTRACT, value(2), value(3)) );
    }
    
    @Test
    public void testParseMultiplicative() {
        assertEval( number(6), "2 * 3", operator(BinaryExpression.Operator.MULTIPLY, value(2), value(3)) );
        assertEval( number(4), "12 / 3", operator(BinaryExpression.Operator.DIVIDE, value(12), value(3)) );
        assertEval( number(2), "12 % 5", operator(BinaryExpression.Operator.REMAINDER, value(12), value(5)) );
    }
    
    private static void assertEval(Value expectedValue, String input, Expression expected) {
        BlockStatement program = assertExpression(input, expected);
        program.execute();
        final Value actual = ScopeHandler.getVariable("a");
        try {
            assertEquals(expectedValue.asNumber(), actual.asNumber(), 0.001);
        } catch (NumberFormatException nfe) {
            assertEquals(expectedValue.asString(), actual.asString());
        }
    }
    
    private static BlockStatement assertExpression(String input, Expression expected) {
        return assertProgram("a = " + input, block(assign("a", expected)));
    }
    
    private static BlockStatement assertProgram(String input, BlockStatement actual) {
        BlockStatement result = (BlockStatement) parse(input);
        assertStatements(result, actual);
        return result;
    }
    
    private static void assertStatements(BlockStatement expected, BlockStatement actual) {
        final int size = expected.statements.size();
        for (int i = 0; i < size; i++) {
            assertEquals(expected.statements.get(i).getClass(), actual.statements.get(i).getClass());
        }
    }

    private static Statement parse(String input) {
        return Parser.parse(Lexer.tokenize(input));
    }
}
