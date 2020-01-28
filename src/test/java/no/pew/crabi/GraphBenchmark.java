package no.pew.crabi;


import no.pew.crabi.graph.NegativeCycle;
import org.openjdk.jmh.annotations.*;

public class GraphBenchmark {


    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void mutableGraphBench(GraphExecutionPlan plan) {
        NegativeCycle.findCycle(plan.mutableGraph);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 1)
    public void immutableGraphBench(GraphExecutionPlan plan) {
        NegativeCycle.findCycle(plan.immutableGraph);
    }

}
