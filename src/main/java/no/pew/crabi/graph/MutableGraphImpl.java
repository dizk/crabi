package no.pew.crabi.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MutableGraphImpl<T, M> implements MutableGraph<T, M> {

    private final Set<Edge<T, M>> edges = new HashSet<>();

    @Override
    public void upsertEdge(T u, T v, double cost, M metaData) {
        Edge<T, M> toAdd = new Edge<>(u, v, cost, metaData);
        edges.remove(toAdd);
        edges.add(toAdd);
    }

    @Override
    public ImmutableGraph<T, M> snapshot() {
        return new ImmutableGraphImpl<>(getN(), edges());
    }

    @Override
    public int getN() {
        return edges
                .stream()
                .flatMap(e -> Stream.of(e.getFrom(), e.getTo()))
                .collect(Collectors.toSet())
                .size();
    }

    @Override
    public Set<Edge<T, M>> edges() {
        return edges
                .stream()
                .map(e -> new Edge<>(e.getFrom(), e.getTo(), e.getCost(), e.getMetaData()))
                .collect(Collectors.toSet());
    }


    @Override
    public String toString() {
        return "MutableGraphImpl{" +
                "edges=" + edges +
                '}';
    }
}
