package no.pew.crabi;


import no.pew.crabi.graph.ImmutableGraph;
import no.pew.crabi.graph.MutableGraphImpl;
import org.openjdk.jmh.annotations.*;

import java.util.Random;

@State(Scope.Benchmark)
public class GraphExecutionPlan {

    @Param({"100"})
    public int size;

    public MutableGraphImpl<Integer, Object> mutableGraph;
    public ImmutableGraph<Integer, Object> immutableGraph;

    @Setup
    public void makeGraphs() {
        MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();
        Random random = new Random();
        int edges = size;
        for (int i = 0; i < edges; i++) {

            int from = random.nextInt(edges / 2);
            int to = random.nextInt(edges / 2);

            while (to == from) {
                from = random.nextInt(edges / 2);
            }

            double weight = (random.nextDouble() * 2 - 1) * 100; // between -1 and 1

            graph.upsertEdge(from, to, weight, null);
        }

        mutableGraph = graph;
        immutableGraph = graph.snapshot();
    }

}
