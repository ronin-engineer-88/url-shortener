package com.roninhub;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

public class KeyGeneratorBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        KeyGeneratorService keyGeneratorService;

        @Setup(Level.Trial)
        public void setUp() throws Exception {
            TokenService tokenService = new MockTokenService();
            keyGeneratorService = new KeyGeneratorService(tokenService);
        }
    }

    // Mirco Benchmark
    @Benchmark
    @BenchmarkMode(Mode.Throughput) // Measure the number of operations per second
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Warmup(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 10, time = 2, timeUnit = TimeUnit.SECONDS)
    @Fork(2)
    public void testGenerateKey(BenchmarkState state) throws Exception {
        state.keyGeneratorService.generateKey();
    }
}