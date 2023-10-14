package com.annimon.ownlang.parser.ast;

import com.annimon.ownlang.lib.ScopeHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.annimon.ownlang.exceptions.VariableDoesNotExistsException;

import static com.annimon.ownlang.parser.ast.ASTHelper.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author aNNiMON
 */
public class VariableExpressionTest {

    @BeforeEach
    void setUp() {
        ScopeHandler.resetScope();
    }
    
    @Test
    public void testVariable() {
        assign("a", value(4)).eval();
        assign("b", value("ABCD")).eval();
        
        assertValue(number(4), var("a").eval());
        assertValue(string("ABCD"), var("b").eval());
    }
    
    @Test
    public void testVariableReplace() {
        assign("a", value(4)).eval();
        assign("a", value(8)).eval();
        
        assertValue(number(8), var("a").eval());
    }
    
    @Test
    public void testUnknownVariable() {
        assertThrows(VariableDoesNotExistsException.class,
                () -> var("a").eval());
    }
}
