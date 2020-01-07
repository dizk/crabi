package no.pew.crabi.graph;

import java.util.Set;

public class ImmutableGraphImpl<T,M> implements ImmutableGraph<T,M> {

    private final int n;
    private final Set<Edge<T,M>> edges;

    public ImmutableGraphImpl(int N, Set<Edge<T,M>> edges) {
        n = N;
        this.edges = edges;
    }

    @Override
    public int getN() {
        return n;
    }

    @Override
    public Set<Edge<T,M>> edges() {
        return edges;
    }

    @Override
    public String toString() {
        return "ImmutableGraphImpl{" +
                "n=" + n +
                ", edges=" + edges +
                '}';
    }
}
