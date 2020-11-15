package no.pew.crabi.graph;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class NegativeCycle {


    public static <T, M> List<List<Pair<T, M>>> findCycle(ImmutableGraph<T, M> graph) {
        Map<T, Double> d = new HashMap<>();
        Map<T, Pair<T, M>> p = new HashMap<>();

        List<Pair<T, M>> inCycle = new ArrayList<>();
        for (int i = 0; i < graph.getN(); ++i) {
            for (Edge<T, M> e : graph.edges()) {
                double distance = d.getOrDefault(e.getFrom(), 0.0) + e.getCost();
                if (distance < d.getOrDefault(e.getTo(), 0.0)) {
                    d.put(e.getTo(), distance);
                    p.put(e.getTo(), Pair.of(e.getFrom(), e.getMetaData()));
                    if (i == graph.getN() - 1) {
                        inCycle.add(Pair.of(e.getTo(), e.getMetaData()));
                    }
                }
            }
        }

        List<List<Pair<T, M>>> cycles = new ArrayList<>();
        for (Pair<T, M> cycleEnd : inCycle) {
            for (int i = 0; i < graph.getN(); ++i)
                cycleEnd = p.get(cycleEnd.getKey());

            List<Pair<T, M>> cycle = new LinkedList<>();
            for (Pair<T, M> v = cycleEnd; ; v = p.get(v.getKey())) {
                cycle.add(v);
                if (v == cycleEnd && cycle.size() > 1)
                    break;
            }

            Collections.reverse(cycle);
            cycles.add(cycle);
        }


        return cycles;
    }
}
