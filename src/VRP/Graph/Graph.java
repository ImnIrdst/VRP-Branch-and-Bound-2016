package VRP.Graph;

import VRP.Algorithms.BranchAndBound.BBGlobalVariables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Simple graph stored in a HashMap adjacency list
 */
public class Graph {
    public Map<String, Vertex> adjacencyList; // adjacency list: mapping of vertex names to Vertex objects, built from a set of Edge

    /**
     * Constructor: Default
     */
    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Constructor: builds a Graph from a csv file
     *
     * @param path: path to the csv file
     */
    public Graph(String path) {
        adjacencyList = new HashMap<>();

        try {
            // read file
            FileInputStream file = new FileInputStream(new File(path));
            Scanner sc = new Scanner(file);

            // read depot Info
            int numberOfVehicles = Integer.parseInt(sc.nextLine().split(",")[1]);
            int fixedCostOfVehicle = Integer.parseInt(sc.nextLine().split(",")[1]);
            int capacityOfVehicle = Integer.parseInt(sc.nextLine().split(",")[1]);

            // fill the global variable
            BBGlobalVariables.vehicleFixedCost = fixedCostOfVehicle;
            BBGlobalVariables.vehicleCapacity  = capacityOfVehicle;

            addVertex(new Vertex("Depot", VertexType.DEPOT, numberOfVehicles, fixedCostOfVehicle, capacityOfVehicle));

            // read customers info
            int numberOfCustomers = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            int cId = 0;
            for (int i = 0; i < numberOfCustomers; i++) {
                String[] tokens = sc.nextLine().split(",");
                addVertex(
                        new Vertex(tokens[0],
                                VertexType.CUSTOMER, cId++,
                                Integer.parseInt(tokens[1]),
                                Integer.parseInt(tokens[2]),
                                Integer.parseInt(tokens[3]),
                                Integer.parseInt(tokens[4]),
                                Integer.parseInt(tokens[5]))
                );
            }

            // fill the global variables
            BBGlobalVariables.numberOfCustomers = numberOfCustomers;

            // read ordinary vertices
            int numberOfOrdinaryVertices = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            for (int i = 0; i < numberOfOrdinaryVertices; i++) {
                addVertex(new Vertex(sc.nextLine(), VertexType.ORDINARY));
            }

            // read edges
            int numberOfEdges = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            for (int i = 0; i < numberOfEdges; i++) {
                String[] tokens = sc.nextLine().split(",");
                addEdge(new Edge(tokens[0], tokens[1], Integer.parseInt(tokens[2])));
                addEdge(new Edge(tokens[1], tokens[0], Integer.parseInt(tokens[2])));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds a vertex to the adjacency list
     */
    public void addVertex(Vertex u) {
        if (!adjacencyList.containsKey(u.name)) adjacencyList.put(u.name, u);
    }

    /**
     * adds an edge to the adjacency list
     */
    public void addEdge(Edge e) {
        if (!adjacencyList.containsKey(e.u)) adjacencyList.put(e.u, new Vertex(e.u));
        if (!adjacencyList.containsKey(e.v)) adjacencyList.put(e.v, new Vertex(e.v));
        adjacencyList.get(e.u).neighbours.put(adjacencyList.get(e.v), e.weight);
    }

    /**
     * Constructor: Builds an adjacencyList from a set of edges
     */
    public Graph(Edge[] edges) {
        adjacencyList = new HashMap<>();

        //one pass to find all vertices
        for (Edge e : edges) {
            addEdge(e);
        }
    }

    /**
     * prints edges of the graph
     */
    public void printGraph(){
        for (Vertex u : adjacencyList.values()){
            for (Vertex v : u.neighbours.keySet()){
                System.out.println(u + " -(" + u.neighbours.get(v) + ")-> " + v );
            }
        }
    }
}