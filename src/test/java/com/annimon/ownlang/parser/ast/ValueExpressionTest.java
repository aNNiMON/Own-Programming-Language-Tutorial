package com.annimon.ownlang.parser.ast;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;
import org.junit.Test;

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
