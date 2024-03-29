package com.annimon.ownlang.modules.ounit;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aNNiMON
 */
public final class ounit implements Module {

    @Override
    public Map<String, Value> constants() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Function> functions() {
        return Map.of(
                "assertEquals", this::assertEquals,
                "assertNotEquals", this::assertNotEquals,
                "assertSameType", this::assertSameType,
                "assertTrue", this::assertTrue,
                "assertFalse", this::assertFalse,
                "runTests", this::runTests
        );
    }
    
    private static String microsToSeconds(long micros) {
        return new DecimalFormat("#0.0000").format(micros / 1000d / 1000d) + " sec";
    }

    private Value assertEquals(Value[] args) {
        Arguments.check(2, args.length);
        if (args[0].equals(args[1])) return NumberValue.ONE;
        throw new OUnitAssertionException("Values are not equal: "
                + "1: " + args[0] + ", 2: " + args[1]);
    }
    
    private Value assertNotEquals(Value[] args) {
        Arguments.check(2, args.length);
        if (!args[0].equals(args[1])) return NumberValue.ONE;
        throw new OUnitAssertionException("Values are equal: " + args[0]);
    }
    
    private Value assertSameType(Value[] args) {
        Arguments.check(2, args.length);
        if (args[0].type() == args[1].type()) return NumberValue.ONE;
        throw new OUnitAssertionException("Types mismatch. "
                + "1: " + Types.typeToString(args[0].type())
                + ", 2: " + Types.typeToString(args[1].type()));
    }
    
    private Value assertTrue(Value[] args) {
        Arguments.check(1, args.length);
        if (args[0].asInt() != 0) return NumberValue.ONE;
        throw new OUnitAssertionException("Expected true, but found false.");
    }
    
    private Value assertFalse(Value[] args) {
        Arguments.check(1, args.length);
        if (args[0].asInt() == 0) return NumberValue.ONE;
        throw new OUnitAssertionException("Expected false, but found true.");
    }

    private Value runTests(Value[] args) {
        final var testFunctions = ScopeHandler.functions().entrySet().stream()
                .filter(e -> e.getKey().toLowerCase().startsWith("test"))
                .toList();
        List<TestInfo> tests = testFunctions.stream()
                .map(e -> runTest(e.getKey(), e.getValue()))
                .toList();

        int failures = 0;
        long summaryTime = 0;
        final StringBuilder result = new StringBuilder();
        for (TestInfo test : tests) {
            if (!test.isPassed) failures++;
            summaryTime += test.elapsedTimeInMicros;
            result.append(Console.newline());
            result.append(test.info());
        }
        result.append(Console.newline());
        result.append(String.format("Tests run: %d, Failures: %d, Time elapsed: %s",
                tests.size(), failures,
                microsToSeconds(summaryTime)));
        return new StringValue(result.toString());
    }
        
    private TestInfo runTest(String name, Function f) {
        final long startTime = System.nanoTime();
        boolean isSuccessfull;
        String failureDescription;
        try {
            f.execute();
            isSuccessfull = true;
            failureDescription = "";
        } catch (OUnitAssertionException oae) {
            isSuccessfull = false;
            failureDescription = oae.getMessage();
        }
        final long elapsedTime = System.nanoTime() - startTime;
        return new TestInfo(name, isSuccessfull, failureDescription, elapsedTime / 1000);
    }
    
    private static class OUnitAssertionException extends RuntimeException {

        public OUnitAssertionException(String message) {
            super(message);
        }
    }

    private record TestInfo(
            String name,
            boolean isPassed,
            String failureDescription,
            long elapsedTimeInMicros
    ) {
        public String info() {
            return "%s [%s]\n%sElapsed: %s\n".formatted(
                    name,
                    isPassed ? "passed" : "FAILED",
                    isPassed ? "" : (failureDescription + "\n"),
                    microsToSeconds(elapsedTimeInMicros)
            );
        }
    }
}
