package no.pew.crabi.graph;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class NegativeCycleTest {

    @Test
    void simpleGraph() {
        MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge(0, 1, -1.0, null);
        graph.upsertEdge(1, 2, -1.0, null);
        graph.upsertEdge(2, 0, -1.0, null);


        List<List<Pair<Integer, Object>>> cycle = NegativeCycle.findCycle(graph);

        assertEquals(3, cycle.size());
//        assertEquals(0, cycle.get(0));
//        assertEquals(1, cycle.get(1));
//        assertEquals(2, cycle.get(2));
//        assertEquals(0, cycle.get(3));


    }

    @Test
    void simpleGraph2() {
        MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge(0, 1, -1.0, null);
        graph.upsertEdge(1, 2, -1.0, null);
        graph.upsertEdge(2, 0, -1.0, null);
        graph.upsertEdge(2, 1, 5.0, null);
        graph.upsertEdge(0, 2, 1.0, null);


        List<List<Pair<Integer, Object>>> cycles = NegativeCycle.findCycle(graph);

        System.out.println(cycles);

        assertEquals(3, cycles.size());

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(0, null));
            add(Pair.of(1, null));
            add(Pair.of(2, null));
            add(Pair.of(0, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(2, null));
            add(Pair.of(0, null));
            add(Pair.of(1, null));
            add(Pair.of(2, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(0, null));
            add(Pair.of(1, null));
            add(Pair.of(2, null));
            add(Pair.of(0, null));
        }}));
    }

    @Test
    void twoSeparateCycles() {
        MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();

        // First negative cycle
        graph.upsertEdge(0, 1, -1.0, null);
        graph.upsertEdge(1, 2, -1.0, null);
        graph.upsertEdge(2, 0, -1.0, null);

        // some extra edges
        graph.upsertEdge(2, 1, 5.0, null);
        graph.upsertEdge(0, 2, 1.0, null);

        // Second negative
        graph.upsertEdge(3, 4, -1.0, null);
        graph.upsertEdge(4, 5, -1.0, null);
        graph.upsertEdge(5, 3, -1.0, null);


        // some extra connecting the cycles
        graph.upsertEdge(3, 1, 3.0, null);
        graph.upsertEdge(5, 2, 1.0, null);
        graph.upsertEdge(0, 4, 1.0, null);
        graph.upsertEdge(4, 0, 1.0, null);


        List<List<Pair<Integer, Object>>> cycles = NegativeCycle.findCycle(graph);

        System.out.println(cycles);


        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(1, null));
            add(Pair.of(2, null));
            add(Pair.of(0, null));
            add(Pair.of(1, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(2, null));
            add(Pair.of(0, null));
            add(Pair.of(1, null));
            add(Pair.of(2, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(0, null));
            add(Pair.of(1, null));
            add(Pair.of(2, null));
            add(Pair.of(0, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(4, null));
            add(Pair.of(5, null));
            add(Pair.of(3, null));
            add(Pair.of(4, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(5, null));
            add(Pair.of(3, null));
            add(Pair.of(4, null));
            add(Pair.of(5, null));
        }}));

        assertTrue(cycles.contains(new ArrayList<Pair<Integer, Object>>() {{
            add(Pair.of(3, null));
            add(Pair.of(4, null));
            add(Pair.of(5, null));
            add(Pair.of(3, null));
        }}));

    }

    @Test
    void noNegativeCycle() {
        MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge(0, 1, 1.0, null);
        graph.upsertEdge(1, 2, 1.0, null);
        graph.upsertEdge(2, 0, 1.0, null);

        List<List<Pair<Integer, Object>>> cycle = NegativeCycle.findCycle(graph);

        assertTrue(cycle.isEmpty());
    }


    void genRandom() {

            MutableGraphImpl<Integer, Object> graph = new MutableGraphImpl<>();
            Random random = new Random();
            int edges = 300;
            for (int i = 0; i < edges; i++) {

                int from = random.nextInt(edges / 2);
                int to = random.nextInt(edges / 2);

                while (to == from) {
                    from = random.nextInt(edges / 2);
                }


                double weight = (random.nextDouble() * 2 - 1) * 100; // between -1 and 1

                graph.upsertEdge(from, to, weight, null);

            }
            NegativeCycle.findCycle(graph);
    }


}