package com.annimon.ownlang.modules.ounit;

import com.annimon.ownlang.Console;
import com.annimon.ownlang.lib.*;
import com.annimon.ownlang.modules.Module;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
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
        final var result = new LinkedHashMap<String, Function>(16);
        result.put("assertEquals", new assertEquals());
        result.put("assertNotEquals", new assertNotEquals());
        result.put("assertSameType", new assertSameType());
        result.put("assertTrue", new assertTrue());
        result.put("assertFalse", new assertFalse());
        result.put("runTests", new runTests());
        return result;
    }
    
    private static String microsToSeconds(long micros) {
        return new DecimalFormat("#0.0000").format(micros / 1000d / 1000d) + " sec";
    }
    
    private static class assertEquals implements Function {
        @Override
        public Value execute(Value[] args) {
            Arguments.check(2, args.length);
            if (args[0].equals(args[1])) return NumberValue.ONE;
            throw new OUnitAssertionException("Values are not equal: "
                    + "1: " + args[0] + ", 2: " + args[1]);
        }
    }
    
    private static class assertNotEquals implements Function {
        @Override
        public Value execute(Value[] args) {
            Arguments.check(2, args.length);
            if (!args[0].equals(args[1])) return NumberValue.ONE;
            throw new OUnitAssertionException("Values are equal: " + args[0]);
        }
    }
    
    private static class assertSameType implements Function {
        @Override
        public Value execute(Value[] args) {
            Arguments.check(2, args.length);
            if (args[0].type() == args[1].type()) return NumberValue.ONE;
            throw new OUnitAssertionException("Types mismatch. "
                    + "1: " + Types.typeToString(args[0].type())
                    + ", 2: " + Types.typeToString(args[1].type()));
        }
    }
    
    private static class assertTrue implements Function {
        @Override
        public Value execute(Value[] args) {
            Arguments.check(1, args.length);
            if (args[0].asInt() != 0) return NumberValue.ONE;
            throw new OUnitAssertionException("Expected true, but found false.");
        }
    }
    
    private static class assertFalse implements Function {
        @Override
        public Value execute(Value[] args) {
            Arguments.check(1, args.length);
            if (args[0].asInt() == 0) return NumberValue.ONE;
            throw new OUnitAssertionException("Expected false, but found true.");
        }
    }
    
    private static class runTests implements Function {
        
        @Override
        public Value execute(Value[] args) {
            List<TestInfo> tests = Functions.getFunctions().entrySet().stream()
                    .filter(e -> e.getKey().toLowerCase().startsWith("test"))
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
    }
    
    private static class OUnitAssertionException extends RuntimeException {

        public OUnitAssertionException(String message) {
            super(message);
        }
    }
    
    private static class TestInfo {
        final String name;
        final boolean isPassed;
        final String failureDescription;
        final long elapsedTimeInMicros;

        public TestInfo(String name, boolean isPassed, String failureDescription, long elapsedTimeInMicros) {
            this.name = name;
            this.isPassed = isPassed;
            this.failureDescription = failureDescription;
            this.elapsedTimeInMicros = elapsedTimeInMicros;
        }
        
        public String info() {
            return String.format("%s [%s]\n%sElapsed: %s\n",
                    name,
                    isPassed ? "passed" : "FAILED",
                    isPassed ? "" : (failureDescription + "\n"),
                    microsToSeconds(elapsedTimeInMicros)
            );
        }
    }
}
