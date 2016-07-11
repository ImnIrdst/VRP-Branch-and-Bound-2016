package VRP.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple graph stored in a HashMap adjacency list
 */
public class Graph {
    public Map<String, Vertex> adjacencyList; // adjacency list: mapping of vertex names to Vertex objects, built from a set of Edge

    /**
     * Constructor: Default
     */
    public Graph(){
        adjacencyList = new HashMap<>();
    }

    /**
     * adds an edge to the adjacency list
     */
    public void addEdge(Edge e){
        if (!adjacencyList.containsKey(e.u)) adjacencyList.put(e.u, new Vertex(e.u));
        if (!adjacencyList.containsKey(e.v)) adjacencyList.put(e.v, new Vertex(e.v));
        adjacencyList.get(e.u).neighbours.put(adjacencyList.get(e.v), e.weight);
    }

    /**
     * Constructor: Builds a adjacencyList from a set of edges
     */
    public Graph(Edge[] edges) {
        adjacencyList = new HashMap<>();

        //one pass to find all vertices
        for (Edge e : edges) {
            addEdge(e);
        }
    }
}