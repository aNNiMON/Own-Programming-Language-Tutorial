package com.annimon.ownlang.parser;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.exceptions.OwnLangParserException;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.ScopeHandler;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.Node;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.error.ParseErrorsFormatterStage;
import com.annimon.ownlang.parser.optimization.OptimizationStage;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import com.annimon.ownlang.stages.*;
import com.annimon.ownlang.util.input.InputSource;
import com.annimon.ownlang.util.input.InputSourceFile;
import com.annimon.ownlang.util.input.SourceLoaderStage;
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
    private static Stage<InputSource, Node> testPipeline;

    public static Stream<InputSource> data() {
        return scanDirectory(RES_DIR)
                .map(File::getPath)
                .map(InputSourceFile::new);
    }

    @BeforeAll
    public static void createStage() {
        testPipeline = new SourceLoaderStage()
                .then(new LexerStage())
                .then(new ParserStage())
                .thenConditional(true, new OptimizationStage(9))
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
    public void testProgram(InputSource inputSource) {
        final StagesDataMap stagesData = new StagesDataMap();
        try {
            testPipeline.perform(stagesData, inputSource);
        } catch (OwnLangParserException ex) {
            final var error = new ParseErrorsFormatterStage()
                    .perform(stagesData, ex.getParseErrors());
            fail(inputSource + "\n" + error, ex);
        } catch (Exception oae) {
            Console.handleException(stagesData, Thread.currentThread(), oae);
            fail(inputSource.toString(), oae);
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
