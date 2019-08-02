package me.scf37.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphTest {
    private Vertex a = new Vertex("a");
    private Vertex b = new Vertex("b");
    private Vertex c = new Vertex("c");
    private Vertex d = new Vertex("d");
    private Vertex e = new Vertex("e");

    @Test
    void testDirectedGraphPath() {
        Graph<Vertex, Edge> g = fillGraph(Graph.build(edgeT, true));

        assertEquals("", pathToString(g.getPath(a, a)));
        assertEquals("acd", pathToString(g.getPath(a, d)));
        assertEquals("ec", pathToString(g.getPath(e, c)));
        assertEquals("ecda", pathToString(g.getPath(e, a)));
        assertEquals("", pathToString(g.getPath(c, e)));
    }

    @Test
    void testUndirectedGraphPath() {
        Graph<Vertex, Edge> g = fillGraph(Graph.build(edgeT, false));


        assertEquals("", pathToString(g.getPath(a, a)));
        assertEquals("ad", pathToString(g.getPath(a, d)));
        assertEquals("ec", pathToString(g.getPath(e, c)));
        assertEquals("eca", pathToString(g.getPath(e, a)));
        assertEquals("ce", pathToString(g.getPath(c, e)));
    }

    private Graph<Vertex, Edge> fillGraph(Graph<Vertex, Edge> g) {
        //  a -> b -> c -> d -> a
        //  a -> c
        //  e -> c
        return g.addVertex(a).addVertex(b).addVertex(c).addVertex(d).addVertex(e)
                .addEdge(new Edge(a, b))
                .addEdge(new Edge(b, c))
                .addEdge(new Edge(c, d))
                .addEdge(new Edge(d, a))
                .addEdge(new Edge(a, c))
                .addEdge(new Edge(e, c));
    }

    private String pathToString(List<Edge> path) {
        String s = "";

        for (Edge e : path) {
            if (s.isEmpty()) {
                s = e.left.toString() + e.right;
            } else {
                s = s + e.right;
            }
        }

        return s;
    }

    private static class Vertex {
        private final String name;

        Vertex(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class Edge {
        Edge(Vertex left, Vertex right) {
            this.left = left;
            this.right = right;
        }

        Vertex left;
        Vertex right;
    }

    private static EdgeT<Vertex, Edge> edgeT = new EdgeT<Vertex, Edge>() {
        @Override
        public Vertex getSource(Edge edge) {
            return edge.left;
        }

        @Override
        public Vertex getTarget(Edge edge) {
            return edge.right;
        }

        @Override
        public Edge reverse(Edge edge) {
            return new Edge(edge.right, edge.left);
        }
    };
}
