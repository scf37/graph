package me.scf37.graph.impl;

import io.vavr.collection.HashMap;
import me.scf37.graph.EdgeT;
import me.scf37.graph.Graph;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Graph implementation.
 *
 * @param <Vertex>
 * @param <Edge>
 */
public class GraphImpl<Vertex, Edge> implements Graph<Vertex, Edge> {
    // This graph is immutable - it is very important property for highly concurrent and yet atomic snapshots.
    // To ensure appropriate performance, graph edges are stored within trie HashMap - immutable data structure
    // optimized to add and remove keys without modification of existing collection.
    //
    // Since we don't support edge removal, edges can be stored within simple immutable linked list

    private final EdgeT<Vertex, Edge> edgeT;
    // trie HashMap - io.vavr.collection.HashMap
    private final HashMap<Vertex, Node<Edge>> edges;
    private final boolean directed;

    public GraphImpl(
            EdgeT<Vertex, Edge> edgeT,
            boolean directed
    ) {
        this(edgeT, HashMap.empty(), directed);
    }

    private GraphImpl(
            EdgeT<Vertex, Edge> edgeT,
            HashMap<Vertex, Node<Edge>> edges,
            boolean directed
    ) {
        this.edgeT = edgeT;
        this.edges = edges;
        this.directed = directed;
    }

    @Override
    public Graph<Vertex, Edge> addVertex(Vertex vertex) {
        if (edges.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " is already in the graph.");
        }
        return copy(edges.put(vertex, Node.empty()));
    }

    @Override
    public Graph<Vertex, Edge> addEdge(Edge edge) {
        Vertex source = edgeT.getSource(edge);
        Vertex target = edgeT.getTarget(edge);

        Node<Edge> nodes = this.edges.getOrElse(source, null);

        if (nodes == null) {
            throw new IllegalArgumentException("Source vertex of edge " + edge + " is not in the graph.");
        }

        if (!this.edges.containsKey(target)) {
            throw new IllegalArgumentException("Target vertex of edge " + edge + " is not in the graph.");
        }

        if (directed) {
            return copy(edges.put(source, new Node<>(edge, nodes)));
        } else {
            return copy(edges
                    .put(source, new Node<>(edge, nodes))
                    .put(target, new Node<>(edgeT.reverse(edge), edges.getOrElse(target, null)))
            );
        }
    }

    @Override
    public List<Edge> getPath(Vertex sourceVertex, Vertex targetVertex) {
        // classic breadth-first search

        if (sourceVertex.equals(targetVertex)) {
            return Collections.emptyList();
        }

        Queue<Vertex> queue = new ArrayDeque<>();
        Set<Vertex> discovered = new HashSet<>();
        Map<Vertex, Edge> parents = new java.util.HashMap<>();

        queue.add(sourceVertex);
        discovered.add(sourceVertex);

        while (!queue.isEmpty()) {
            Vertex v = queue.poll();

            Node<Edge> children = edges.getOrElse(v, Node.empty());
            while (children.edge != null) {
                Vertex target = edgeT.getTarget(children.edge);

                if (!discovered.contains(target)) {
                    queue.add(target);
                    discovered.add(target);
                    parents.put(target, children.edge);
                }

                if (target.equals(targetVertex)) {
                    LinkedList<Edge> path = new LinkedList<>();
                    reconstructPath(parents, sourceVertex, targetVertex, path);
                    return path;
                }

                children = children.next;
            }
        }

        return Collections.emptyList();
    }

    private void reconstructPath(Map<Vertex, Edge> parents, Vertex sourceVertex, Vertex targetVertex, Deque<Edge> path) {
        if (sourceVertex.equals(targetVertex)) {
            return;
        }

        Edge edge = parents.get(targetVertex);
        path.push(edge);
        reconstructPath(parents, sourceVertex, edgeT.getSource(edge), path);
    }

    private Graph<Vertex, Edge> copy(HashMap<Vertex, Node<Edge>> edges) {
        return new GraphImpl<>(edgeT, edges, directed);
    }

    private static final class Node<T> {
        final T edge;
        final Node<T> next;

        Node(T edge, Node<T> next) {
            this.edge = edge;
            this.next = next;
        }

        private static Node empty = new Node<>(null, null);

        @SuppressWarnings("unchecked")
        static <T> Node<T> empty() {
            return (Node<T>) empty;
        }
    }
}
