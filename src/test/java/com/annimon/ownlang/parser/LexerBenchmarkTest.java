package com.annimon.ownlang.parser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
public class LexerBenchmarkTest {

    @Param({"1000"})
    private int iterations;

    private String input;

    @Setup(Level.Trial)
    public void initializeTrial() throws IOException {
        input = SourceLoader.readSource("program.own");
    }

    @Benchmark
    public void lexerBenchmark() {
        for (int i = 0; i < iterations; i++) {
            Lexer.tokenize(input);
        }
    }

    //@Test
    public void executeBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LexerBenchmarkTest.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
