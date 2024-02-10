package com.annimon.ownlang.parser;

import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.stages.Stage;
import com.annimon.ownlang.stages.StagesData;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class MockOUnitStage implements Stage<Node, Node> {

    @Override
    public Node perform(StagesData stagesData, Node input) {
        ScopeHandler.resetScope();
        ScopeHandler.setFunction("assertEquals", (args) -> {
            assertEquals(args[0], args[1]);
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("assertNotEquals", (args) -> {
            assertNotEquals(args[0], args[1]);
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("assertSameType", (args) -> {
            assertEquals(args[0].type(), args[1].type());
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("assertTrue", (args) -> {
            assertTrue(args[0].asInt() != 0);
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("assertFalse", (args) -> {
            assertFalse(args[0].asInt() != 0);
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("assertFail", (args) -> {
            assertThrows(Throwable.class,
                    () -> ((FunctionValue) args[0]).getValue().execute());
            return NumberValue.ONE;
        });
        ScopeHandler.setFunction("fail", (args) -> {
            if (args.length > 0) {
                fail(args[0].asString());
            } else {
                fail();
            }
            return NumberValue.ONE;
        });
        return input;
    }
}
