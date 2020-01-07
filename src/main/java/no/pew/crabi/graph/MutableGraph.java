package no.pew.crabi.graph;

public interface MutableGraph<T, M> extends ImmutableGraph<T, M> {
    void upsertEdge(T u, T v, double cost, M metaData);

    ImmutableGraph<T, M> snapshot();
}
