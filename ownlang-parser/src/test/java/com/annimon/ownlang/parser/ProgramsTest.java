package com.annimon.ownlang.parser;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.FunctionValue;
import com.annimon.ownlang.lib.Functions;
import com.annimon.ownlang.lib.NumberValue;
import com.annimon.ownlang.lib.Variables;
import com.annimon.ownlang.outputsettings.OutputSettings;
import com.annimon.ownlang.outputsettings.StringOutputSettings;
import com.annimon.ownlang.parser.ast.FunctionDefineStatement;
import com.annimon.ownlang.parser.ast.Statement;
import com.annimon.ownlang.parser.ast.Visitor;
import com.annimon.ownlang.parser.visitors.AbstractVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramsTest {
    private static final String RES_DIR = "src/test/resources";

    public static Stream<String> data() {
        final File resDir = new File(RES_DIR);
        return scanDirectory(resDir)
                .map(File::getPath);
    }

    private static Stream<File> scanDirectory(File dir) {
        final File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return Stream.empty();
        }
        return Arrays.stream(files)
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return scanDirectory(file);
                    }
                    return Stream.of(file);
                })
                .filter(f -> f.getName().endsWith(".own"));
    }

    @BeforeEach
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
        Functions.set("assertFail", (args) -> {
            assertThrows(Throwable.class,
                    () -> ((FunctionValue) args[0]).getValue().execute());
            return NumberValue.ONE;
        });
    }

    @ParameterizedTest
    @MethodSource("data")
    public void testProgram(String programPath) throws IOException {
        final String source = SourceLoader.readSource(programPath);
        final Statement s = Parser.parse(Lexer.tokenize(source));
        try {
            s.execute();
            s.accept(testFunctionsExecutor);
        } catch (Exception oae) {
            fail(oae.toString());
        }
    }

    @Test
    public void testOutput() throws IOException {
        OutputSettings oldSettings = Console.getSettings();
        Console.useSettings(new StringOutputSettings());
        String source = "for i = 0, i <= 5, i++\n  print i";
        final Statement s = Parser.parse(Lexer.tokenize(source));
        try {
            s.execute();
            assertEquals("012345", Console.text());
        } catch (Exception oae) {
            fail(oae.toString());
        } finally {
            Console.useSettings(oldSettings);
        }
    }

    private static Visitor testFunctionsExecutor = new AbstractVisitor() {
        @Override
        public void visit(FunctionDefineStatement s) {
            if (s.name.startsWith("test")) {
                try {
                    Functions.get(s.name).execute();
                } catch (AssertionError err) {
                    throw new AssertionError(s.name + ": " + err.getMessage(), err);
                }
            }
        }
    };
}
