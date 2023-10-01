package com.annimon.ownlang.parser;

import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import com.annimon.ownlang.stages.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.io.File;
import java.util.stream.Stream;
import static com.annimon.ownlang.parser.TestDataUtil.scanDirectory;
import static org.junit.jupiter.api.Assertions.*;

public class ProgramsTest {
    private static final String RES_DIR = "src/test/resources";
    private static Stage<String, Statement> testPipeline;

    public static Stream<String> data() {
        return scanDirectory(RES_DIR)
                .map(File::getPath);
    }

    @BeforeAll
    public static void createStage() {
        testPipeline = new SourceLoaderStage()
                .then(new LexerStage())
                .then(new ParserStage())
                .then(new ExecutionStage())
                .then((stagesData, input) -> {
                    input.accept(testFunctionsExecutor);
                    return input;
                });
    }

    @BeforeEach
    public void initialize() {
        ScopeHandler.resetScope();
        // Let's mock junit methods as ounit functions
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
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testProgram(String programPath) {
        try {
            testPipeline.perform(new StagesDataMap(), programPath);
        } catch (Exception oae) {
            fail(oae);
        }
    }

    private static final Visitor testFunctionsExecutor = new AbstractVisitor() {
        @Override
        public void visit(FunctionDefineStatement s) {
            if (s.name.startsWith("test")) {
                try {
                    ScopeHandler.getFunction(s.name).execute();
                } catch (AssertionError err) {
                    throw new AssertionError(s.name + ": " + err.getMessage(), err);
                }
            }
        }
    };
}
