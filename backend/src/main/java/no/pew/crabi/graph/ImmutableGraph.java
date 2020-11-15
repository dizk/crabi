package no.pew.crabi.graph;

import java.util.Set;

public interface ImmutableGraph<T,M> {

    int getN();

    Set<Edge<T,M>> edges();


}
