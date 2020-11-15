package no.pew.crabi.graph;

import java.util.Objects;

public class Edge<T,M> {

    private final T from;
    private final T to;
    private final double cost;
    private final M metaData;

    Edge(T from, T to, double cost, M metaData) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.metaData = metaData;
    }

    T getFrom() {
        return from;
    }

    T getTo() {
        return to;
    }

    double getCost() {
        return cost;
    }

    public M getMetaData() {
        return metaData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from == edge.from &&
                to == edge.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "\nEdge{" +
                "from=" + from +
                ", to=" + to +
                ", cost=" + cost +
                ", metaData=" + metaData +
                '}';
    }
}
