package com.annimon.ownlang.parser;

import com.annimon.ownlang.parser.ast.Statement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static com.annimon.ownlang.parser.TestDataUtil.scanDirectory;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ProgramsBenchmarkTest {
    private static final String RES_DIR = "src/test/resources/benchmarks";

    @Param({"10"})
    private int iterations;
    @Param({"-"})
    private String path;

    private Statement program;

    @Setup(Level.Trial)
    public void initializeTrial() throws IOException {
        if (!Files.exists(Path.of(path))) return;
        final var source = SourceLoader.readSource(path);
        final var tokens = Lexer.tokenize(source);
        program = Parser.parse(tokens);
    }

    @Benchmark
    public void programBenchmark() {
        for (int i = 0; i < iterations; i++) {
            program.execute();
        }
    }

    @Disabled
    @Test
    public void executeBenchmark() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ProgramsBenchmarkTest.class.getSimpleName())
                .warmupIterations(3)
                .measurementIterations(5)
                .param("path", scanDirectory(RES_DIR)
                        .map(File::getPath)
                        .toArray(String[]::new))
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
