package VRP.Graph;

import VRP.GlobalVars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

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
     * builds a Graph from a csv file
     *
     * @param path: path to the csv file
     */
    public static Graph buildAGraphFromCSVFile(String path) {
        Graph graph = new Graph();
        try {
            // read file
            FileInputStream file = new FileInputStream(new File(path));
            Scanner sc = new Scanner(file);

            // read depot Info
            int numberOfVehicles = Integer.parseInt(sc.nextLine().split(",")[1]);
            int fixedCostOfVehicle = Integer.parseInt(sc.nextLine().split(",")[1]);
            int capacityOfVehicle = Integer.parseInt(sc.nextLine().split(",")[1]);
            int depotDueDate = Integer.parseInt(sc.nextLine().split(",")[1]);
            int depotPenalty = Integer.parseInt(sc.nextLine().split(",")[1]);

            // fill the global variable
            GlobalVars.numberOfVehicles = numberOfVehicles;
            GlobalVars.vehicleFixedCost = fixedCostOfVehicle;
            GlobalVars.vehicleCapacity = capacityOfVehicle;

            graph.addVertex(new Vertex(GlobalVars.depotName, VertexType.DEPOT,
                    numberOfVehicles, fixedCostOfVehicle, capacityOfVehicle, depotDueDate, depotPenalty, true));

            // read customers info
            int numberOfCustomers = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            // filling global variables
            GlobalVars.customerDemands = new ArrayList<>();

            int cId = 0;
            for (int i = 0; i < numberOfCustomers; i++) {
                String[] tokens = sc.nextLine().split(",");
                Vertex newVertex = new Vertex(tokens[0],
                        VertexType.CUSTOMER, cId,
                        Integer.parseInt(tokens[1]),
                        Integer.parseInt(tokens[2]),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]));

                graph.addVertex(newVertex);

                // filling global variables
                GlobalVars.customerDemands.set(cId++, newVertex.demand);
            }

            // fill the global variables
            GlobalVars.numberOfCustomers = numberOfCustomers;

            // read ordinary vertices
            int numberOfOrdinaryVertices = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            for (int i = 0; i < numberOfOrdinaryVertices; i++) {
                graph.addVertex(new Vertex(sc.nextLine(), VertexType.ORDINARY));
            }

            // read edges
            int numberOfEdges = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            for (int i = 0; i < numberOfEdges; i++) {
                String[] tokens = sc.nextLine().split(",");
                graph.addEdge(new Edge(tokens[0], tokens[1], Integer.parseInt(tokens[2])));
            }

            return graph;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * build a Graph from an attribute table
     *
     * @param nodesFilePath: path to the attribute table (csv) that contains nodes
     * @param roadsFilePath: path to the attribute table (csv) that contains roads
     */
    public static Graph buildAGraphFromAttributeTables(String nodesFilePath, String roadsFilePath) {
        Graph graph = new Graph();
        HashMap<String, Vertex> coordsToVertexMap = new HashMap<>();

        // read Nodes
        try {
            FileInputStream file = new FileInputStream(new File(nodesFilePath));
            Scanner sc = new Scanner(file);
            sc.nextLine();

            while (sc.hasNextLine()) {
                Vertex newVertex = Vertex.buildAVertexFromAttributeTableRow(sc.nextLine());
                graph.addVertex(newVertex);

                coordsToVertexMap.put(newVertex.coords, newVertex);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        // read roads

        try {
            FileInputStream file = new FileInputStream(new File(roadsFilePath));
            Scanner sc = new Scanner(file);
            sc.nextLine();

            while (sc.hasNextLine()) {
                Edge edge = Edge.buildEdgeFromAttributeTableRow(sc.nextLine(), coordsToVertexMap);
                graph.addEdge(edge);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return graph;
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
        adjacencyList.get(e.v).neighbours.put(adjacencyList.get(e.u), e.weight);
    }

    /**
     * check if the graph contains this vertex name or not
     */
    public boolean containsVertex(String startName) {
        return adjacencyList.containsKey(startName);
    }

    /**
     * prints edges of the graph
     */
    public void printGraph() {
        for (Vertex u : adjacencyList.values()) {
            for (Vertex v : u.neighbours.keySet()) {
                System.out.println(u + " -(" + u.neighbours.get(v) + ")-> " + v);
            }
        }
    }

    /**
     * @return the vertex with given name
     */
    public Vertex getVertexByName(String startName) {
        return adjacencyList.get(startName);
    }

    /**
     * @return list of vertices in the graph
     */
    public Collection<Vertex> getVertices() {
        return adjacencyList.values();
    }

    /**
     * @return distance between to nodes (by name)
     */
    public double getDistance(String uName, String vName) {
        Vertex u = getVertexByName(uName);
        Vertex v = getVertexByName(vName);
        return getDistance(u, v);
    }

    /**
     * @return distance between to nodes (by vertex)
     */
    public double getDistance(Vertex u, Vertex v) {
        return u.neighbours.get(v);
    }

}