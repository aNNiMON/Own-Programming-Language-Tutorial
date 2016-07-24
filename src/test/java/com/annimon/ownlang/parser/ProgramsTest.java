package com.annimon.ownlang.parser;

import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class ProgramsTest {

    private static final String RES_DIR = "src/test/resources";

    private final String programPath;

    public ProgramsTest(String programPath) {
        this.programPath = programPath;
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<String> data() {
        final File resDir = new File(RES_DIR);
        return scanDirectory(resDir)
                .map(f -> f.getPath())
                .collect(Collectors.toList());
    }

    private static Stream<File> scanDirectory(File dir) {
        return Arrays.stream(dir.listFiles())
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return scanDirectory(file);
                    }
                    return Stream.of(file);
                })
                .filter(f -> f.getName().endsWith(".own"));
    }

    @Before
    public void initialize() {
        Variables.clear();
        Functions.clear();
        // Let's mock junit methods as ounit functions
        Functions.set("assertEquals", (args) -> {
            assertEquals(args[0], args[1]);
            return NumberValue.ONE;
        });
        Functions.set("assertNotEquals", (args) -> {
            assertNotEquals(args[0], args[1]);
            return NumberValue.ONE;
        });
        Functions.set("assertSameType", (args) -> {
            assertEquals(args[0].type(), args[1].type());
            return NumberValue.ONE;
        });
        Functions.set("assertTrue", (args) -> {
            assertTrue(args[0].asInt() != 0);
            return NumberValue.ONE;
        });
        Functions.set("assertFalse", (args) -> {
            assertFalse(args[0].asInt() != 0);
            return NumberValue.ONE;
        });
    }

    @Test
    public void testProgram() throws IOException {
        final String source = SourceLoader.readSource(programPath);
        final Statement s = Parser.parse(Lexer.tokenize(source));
        try {
            s.execute();
            s.accept(testFunctionsExecutor);
        } catch (Exception oae) {
            Assert.fail(oae.toString());
        }
    }

    private static Visitor testFunctionsExecutor = new AbstractVisitor() {
        @Override
        public void visit(FunctionDefineStatement s) {
            if (s.name.startsWith("test")) {
                Functions.get(s.name).execute();
            }
        }
    };
}
