package com.annimon.ownlang.parser.ast;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;
import com.annimon.ownlang.exceptions.VariableDoesNotExistsException;
import org.junit.Test;

/**
 *
 * @author aNNiMON
 */
public class VariableExpressionTest {
    
    @Test
    public void testVariable() {
        assign("a", value(4)).execute();
        assign("b", value("ABCD")).execute();
        
        assertValue(number(4), var("a").eval());
        assertValue(string("ABCD"), var("b").eval());
    }
    
    @Test
    public void testVariableReplace() {
        assign("a", value(4)).execute();
        assign("a", value(8)).execute();
        
        assertValue(number(8), var("a").eval());
    }
    
    @Test(expected = VariableDoesNotExistsException.class)
    public void testUnknownVariable() {
        var("a").eval();
    }
}
