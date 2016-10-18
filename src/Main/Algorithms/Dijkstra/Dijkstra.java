package Main.Algorithms.Dijkstra;

import Main.Graph.Edge;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Dijkstra algorithm (Just give it the Main.Graph)
 */
public class Dijkstra {
    private Graph graph;

    /**
     * Constructor
     */
    public Dijkstra(Graph graph) { // constructor
        this.graph = graph;
    }

    /**
     * Runs dijkstra using a specified source vertex O(nlogn + E)
     */
    public void run(String startName) {

        if (!graph.containsVertex(startName)) { // if you set the wrong node to start dijkstra with it
            System.err.printf("Main.Graph doesn't contain start vertex \"%s\"\n", startName);
            return;
        }

        Vertex source = graph.getVertexByName(startName); // extract the source vertex using it's name

        // builds a min heap for extracting minimum distance vertex in log(n) [https://en.wikipedia.org/wiki/Binary_heap]
        PriorityQueue<Vertex> pq = new PriorityQueue<>(10, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                return Double.compare(v1.distOnShortestPath, v2.distOnShortestPath);
            }
        });

        // initialize vertices
        for (Vertex v : graph.getVertices()) {
            if (v == source) {
                v.distOnShortestPath = 0;
                v.previousNodeOnShortestPath = source; // for start node distance is 0 and previous node is itself
            } else {
                v.distOnShortestPath = Integer.MAX_VALUE;
                v.previousNodeOnShortestPath = null; // for all other nodes set their distance to infinity and previous to null
            }
            pq.add(v); // add node to priority queue
        }

        while (!pq.isEmpty()) {
            Vertex u = pq.poll(); // vertex with shortest distance (first iterations will return source)
            if (u.distOnShortestPath == Integer.MAX_VALUE)
                break; // we can ignore u (and any other remaining vertices) since they are unreachable

            //look at distances to each neighbour
            for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
                Vertex v = a.getKey(); //the neighbour in this iterations

                double weight = a.getValue();
                if (u.distOnShortestPath + weight < v.distOnShortestPath) { // shorter path to neighbour found
                    pq.remove(v);             // remove it from pq
                    v.distOnShortestPath = u.distOnShortestPath + weight; // update its distance
                    v.previousNodeOnShortestPath = u;           // update its previous node on the shortest path
                    pq.add(v);                // add it to the pq again
                }
            }
        }
    }

    /**
     * Prints a path from the source to the specified vertex
     */
    public void printPath(String endName) {
        if (!graph.containsVertex(endName)) { // if you set the wrong node to print the path
            System.err.printf("Main.Graph doesn't contain end vertex \"%s\"\n", endName);
            return;
        }

        System.out.print("Shortest path for node " + endName + ": ");
        System.out.print(graph.getVertexByName(endName).getPrintPathString());
        System.out.println();
    }

    /**
     * used for wtk exporting
     */
    public String getTheShortestPathEdgesWTKStringBetweenSourceAndTheNode(String endNodeName) {
        String[] nodeNames = graph.getVertexByName(endNodeName).getPrintPathString().split(" -> ");

        StringBuilder result = new StringBuilder("");
        for (int i = 0; i < nodeNames.length-1; i++) {
            result.append(graph.getEdgeWTK(nodeNames[i].split(" ")[0], nodeNames[i + 1].split(" ")[0])).append("\n");
        }
        return result.toString();
    }

    /**
     * Prints the path from the source to every vertex (output order is not guaranteed)
     */
    public void printAllPaths() {
        for (Vertex v : graph.getVertices()) {
            printPath(v.name);
        }
    }

    public String getTheShortestPathEdgesWTKStringBetweenTwoNodes(String u, String v) {
        this.run(u.split("\\p{Ps}")[0]);
        return getTheShortestPathEdgesWTKStringBetweenSourceAndTheNode(v.split("\\p{Ps}")[0]);
    }

    /**
     * Make ShortestPath graph
     **/
    public Graph makeShortestPathGraph() {
        Graph shortestPathGraph = new Graph();
        for (Vertex u : graph.getVertices()) {
            if (u.type != VertexType.ORDINARY) {
                shortestPathGraph.addVertex(new Vertex(u));
            }
        }

        for (Vertex u : graph.getVertices()) {
            if (u.type == VertexType.ORDINARY) continue;

            this.run(u.name);
            for (Vertex v : graph.getVertices()) {
                if (v.type == VertexType.ORDINARY || u.name.equals(v.name)) continue;
                shortestPathGraph.addEdge(new Edge(u.name, v.name, v.distOnShortestPath));

            }
        }

        return shortestPathGraph;
    }
}