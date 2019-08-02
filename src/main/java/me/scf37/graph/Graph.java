package me.scf37.graph;

import me.scf37.graph.impl.GraphImpl;

import java.util.List;

/**
 * Directed or undirected graph operating on any Vertex and Edge types.
 * <p>
 * Vertex type must implement equals and hashcode
 * Edge type has no requirements, however singleton instance of EdgeT is required to let Graph operate on
 * provided Edge type.
 * <p>
 * This Graph is immutable - and therefore safe for concurrent usage. Users are encouraged to use its instances
 * within immutable data classes or AtomicReference for concurrent, atomic, consistent updates.
 *
 * @param <Vertex> type of graph vertex, must implement correct equals and hashcode
 * @param <Edge>   type of graph edge
 */
public interface Graph<Vertex, Edge> {

    /**
     * Add vertex to this immutable graph
     *
     * @param vertex vertex to add
     * @return graph with this vertex added
     * @throws IllegalArgumentException if vertex already exists in this graph
     */
    Graph<Vertex, Edge> addVertex(Vertex vertex);

    /**
     * Add edge to this immutable graph. Duplicate edges are allowed.
     *
     * @param edge edge to add
     * @return graph with edge added
     * @throws IllegalArgumentException if either vertex of this edge is not in this graph
     */
    Graph<Vertex, Edge> addEdge(Edge edge);

    /**
     * Calculate any path from sourceVertex to targetVertex.
     * <p>
     * If there is no path or sourceVertex == targetVertex, empty list is returned
     *
     * @param sourceVertex source vertex
     * @param targetVertex target vertex
     * @return path between vertexes or empty list
     */
    List<Edge> getPath(Vertex sourceVertex, Vertex targetVertex);

    /**
     * Create new Graph instance.
     *
     * @param edgeT    Edge typeclass, containing operations on Edge required by Graph
     * @param directed if true, build directed graph, undirected otherwise
     * @param <Vertex> Vertex type, must implement equals and hashcode
     * @param <Edge>   Edge type, no requirements
     * @return Graph instance
     */
    static <Vertex, Edge> Graph<Vertex, Edge> build(EdgeT<Vertex, Edge> edgeT, boolean directed) {
        return new GraphImpl<>(edgeT, directed);
    }
}
