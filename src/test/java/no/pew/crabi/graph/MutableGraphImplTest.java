package no.pew.crabi.graph;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MutableGraphImplTest {


    @Test
    void upsertEdge() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge("1", "2", 0.0, null);

        assertEquals(1, graph.edges().size());
    }

    @Test
    void checkEdgeWasAdded() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge("1", "2", 0.0, null);


        Set<Edge<String, Object>> edges = graph.edges();
        assertTrue(edges.contains(new Edge<>("1", "2", 0.0, null)));
    }

    @Test
    void checkCost() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();
        graph.upsertEdge("1", "2", 0.0, null);


        Set<Edge<String, Object>> edges = graph.edges();
        assertEquals(0.0, edges.iterator().next().getCost());
    }

    @Test
    void checkCostIsUpdated() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();

        graph.upsertEdge("1", "2", 0.0, null);
        assertEquals(0.0, graph.edges().iterator().next().getCost());

        graph.upsertEdge("1", "2", 1.0, null);
        assertEquals(1.0, graph.edges().iterator().next().getCost());
    }

    @Test
    void takeSnapshot() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();

        graph.upsertEdge("1", "2", 0.0, null);

        ImmutableGraph<String, Object> immuGraph = graph.snapshot();
        assertEquals(0.0, immuGraph.edges().iterator().next().getCost());
    }

    @Test
    void checkSnapshotIsNotUpdated() {
        MutableGraphImpl<String, Object> graph = new MutableGraphImpl<>();

        graph.upsertEdge("1", "2", 0.0, null);

        ImmutableGraph<String, Object> immuGraph = graph.snapshot();


        // Update cost
        graph.upsertEdge("1", "2", 1.0, null);

        // Should not be updated
        assertEquals(0.0, immuGraph.edges().iterator().next().getCost());
    }
}