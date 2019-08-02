package me.scf37.graph;

/**
 * Edge typeclass - defines operations on abstract Edge required by Graph instance
 *
 * @param <Vertex>
 * @param <Edge>
 */
public interface EdgeT<Vertex, Edge> {
    /**
     * Get source vertex of the edge
     *
     * @param edge edge
     * @return source vertex
     */
    Vertex getSource(Edge edge);

    /**
     * Get target vertex of the edge
     *
     * @param edge edge
     * @return target vertex
     */
    Vertex getTarget(Edge edge);

    /**
     * Reverse edge direction, i.e. from source -> target to target -> source
     *
     * @param edge
     * @return reversed edge
     */
    Edge reverse(Edge edge);
}
