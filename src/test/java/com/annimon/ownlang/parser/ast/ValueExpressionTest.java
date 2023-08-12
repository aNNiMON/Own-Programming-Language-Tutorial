package com.annimon.ownlang.parser.ast;

import org.junit.jupiter.api.Test;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;

/**
 *
 * @author aNNiMON
 */
public class ValueExpressionTest {
    
    @Test
    public void testValue() {
        assertValue(number(4), value(4).eval());
        assertValue(string("ABCD"), value("ABCD").eval());
    }
}
