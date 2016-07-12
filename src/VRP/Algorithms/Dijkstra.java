package VRP.Algorithms;

import VRP.Graph.Edge;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
/**
 * Dijkstra algorithm (Just give it the Graph)
 */
public class Dijkstra {
    private Map<String, Vertex> adjacencyList; // the adjacencyList of the graph (provided by user)

    public Dijkstra(Graph graph){ // constructor
        this.adjacencyList = graph.adjacencyList;
    }

    /** Runs dijkstra using a specified source vertex O(nlogn + E)*/
    public void run(String startName) {

        if (!adjacencyList.containsKey(startName)) { // if you set the wrong node to start dijkstra with it
            System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
            return;
        }

        Vertex source = adjacencyList.get(startName); // extract the source vertex using it's name

        // builds a min heap for extracting minimum distance vertex in log(n) [https://en.wikipedia.org/wiki/Binary_heap]
        PriorityQueue<Vertex> pq = new PriorityQueue<>(10, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex v1, Vertex v2) {
                return Integer.compare(v1.distOnShortestPath, v2.distOnShortestPath);
            }
        });

        // initialize vertices
        for (Vertex v : adjacencyList.values()) {
            if (v == source){
                v.distOnShortestPath = 0; v.previousNodeOnShortestPath = source; // for start node distance is 0 and previous node is itself
            } else {
                v.distOnShortestPath = Integer.MAX_VALUE; v.previousNodeOnShortestPath = null; // for all other nodes set their distance to infinity and previous to null
            }
            pq.add(v); // add node to priority queue
        }

        while (!pq.isEmpty()) {
            Vertex u = pq.poll(); // vertex with shortest distance (first iteration will return source)
            if (u.distOnShortestPath == Integer.MAX_VALUE) break; // we can ignore u (and any other remaining vertices) since they are unreachable

            //look at distances to each neighbour
            for (Map.Entry<Vertex, Integer> a : u.neighbours.entrySet()) {
                Vertex v = a.getKey(); //the neighbour in this iteration

                int weight = a.getValue();
                if (u.distOnShortestPath + weight < v.distOnShortestPath) { // shorter path to neighbour found
                    pq.remove(v);             // remove it from pq
                    v.distOnShortestPath = u.distOnShortestPath + weight; // update its distance
                    v.previousNodeOnShortestPath = u;           // update its previous node on the shortest path
                    pq.add(v);                // add it to the pq again
                }
            }
        }
    }

    /** Prints a path from the source to the specified vertex */
    public void printPath(String endName) {
        if (!adjacencyList.containsKey(endName)) { // if you set the wrong node to print the path
            System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
            return;
        }

        System.out.print("Shortest path for node " + endName + ": ");
        adjacencyList.get(endName).printPath();
        System.out.println();
    }

    /** Prints the path from the source to every vertex (output order is not guaranteed) */
    public void printAllPaths() {
        for (Vertex v : adjacencyList.values()) {
            printPath(v.name);
        }
    }

    /** Make Shortestpath graph **/
    public Graph makeShortestPathGraph(){
        Graph graph = new Graph();
        for (Vertex u: adjacencyList.values())
            graph.addVertex(new Vertex(u));

        for (Vertex u: adjacencyList.values()){
            this.run(u.name);

            for (Vertex v: adjacencyList.values()){
                graph.addEdge(new Edge(u.name, v.name, v.distOnShortestPath));

            }
        }

        return graph;
    }
}