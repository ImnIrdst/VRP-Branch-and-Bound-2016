package Main.Graph;

import Main.Algorithms.Heuristics.DispatchingRules.RankingIndex;
import Main.Algorithms.Other.Random;
import Main.Algorithms.Other.Random.IRange;
import Main.Algorithms.Other.Random.DRange;
import Main.GlobalVars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Simple graph stored in a HashMap adjacency customers
 */
public class Graph {
    public Map<String, Vertex> adjacencyList; // adjacency customers: mapping of vertex names to Vertex objects, built from a setCustomer of Edge
    public Map<Integer, Vertex> idToVertexMap; // maps vertex Id to Vertex

    /**
     * Constructor: Default
     */
    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Constructor: Builds an adjacencyList from a setCustomer of edges
     */
    public Graph(Edge[] edges) {
        adjacencyList = new HashMap<>();

        //one pass to find all vertices
        for (Edge e : edges) {
            addEdge(e);
        }
    }

//    public static Graph buildRandomGraphFromDoubleTestCase(
//            Main.AutomatedTests.TestCases.DoubleTestCase.SCSTestCase testCase, int seed) {
//        Random.setSeed(seed);
//
//        Graph graph = new Graph();
//        // Add Depot Vertex
//        int capacity = (int) (testCase.customerQty * Random.getRandomDoubleInRange(testCase.capacityRange));
//        graph.addVertex(new Vertex("Dp", VertexType.DEPOT, testCase.vehicleQty, capacity, testCase.fixCost));
//
//        // Build the Customer Vertices
//        double sumOfProcessTimes = 0;
//        for (int i = 0; i < testCase.customerQty; i++) {
//            String name = "" + (char) ('A' + i);
//            double processTime = Random.getRandomDoubleInRange(testCase.processTimeRange);
//            double dueDate = Random.getRandomDoubleInRange(testCase.dueDateRange);
//            double deadline = Random.getRandomDoubleInRange(testCase.)
//            double penalty = Random.getRandomDoubleInRange(testCase.penaltyRange);
//            graph.addVertex(new Vertex(name, VertexType.CUSTOMER, processTime, dueDate, deadline, penalty));
//
//            sumOfProcessTimes += processTime;
//        }
//
//        // Add The Edges
//        for (Vertex u : graph.getVertices()) {
//            u.dueDate *= sumOfProcessTimes; // (0.4*sumOfProcessTimes - 0.7*sumOfProcessTimes)
//            for (Vertex v : graph.getVertices()) {
//                if (u.name.equals(v.name)) continue;
//                Edge e = new Edge(u, v, Random.getRandomDoubleInRange(testCase.edgeWeightRange));
//                graph.addEdge(e);
//            }
//        }
//
//        return graph;
//    }

    public static Graph buildRandomGraphFromIntegerTestCase(
            Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestCase testCase, int seed) {
        Random.setSeed(seed);

        Graph graph = new Graph();
        // Add Depot Vertex
        int capacity = (int) (testCase.customerQty * Random.getRandomDoubleInRange(testCase.capacityRange));
        graph.addVertex(new Vertex("Dp", VertexType.DEPOT, testCase.vehicleQty, capacity, testCase.fixCost));

        // Build the Customer Vertices
        double sumOfProcessTimes = 0;
        for (int i = 0; i < testCase.customerQty; i++) {
            String name = "" + (char) ('A' + i);
            int processTime = Random.getRandomIntInRange(testCase.processTimeRange);
            double dueDate = Random.getRandomDoubleInRange(testCase.dueDateRange);
            double deadLine = dueDate + Random.getRandomDoubleInRange(testCase.deadLineRange);
            int penalty = Random.getRandomIntInRange(testCase.penaltyRange);
            int maxGain = Random.getRandomIntInRange(testCase.maxGainRange);
            graph.addVertex(new Vertex(name, VertexType.CUSTOMER, processTime, dueDate, deadLine, penalty, maxGain));


            sumOfProcessTimes += processTime;
        }

        // Add The Edges
        for (Vertex u : graph.getVertices()) {
            u.dueDate = (int)(u.dueDate * sumOfProcessTimes); // (0.4*sumOfProcessTimes - 0.7*sumOfProcessTimes)
            for (Vertex v : graph.getVertices()) {
                if (u.name.equals(v.name)) continue;
                Edge e = new Edge(u, v, Random.getRandomIntInRange(testCase.edgeWeightRange));
                graph.addEdge(e);
            }
        }

        return graph;
    }

    /**
     * Sets the ids of vertexes in a graph, depot is n and customers are (0 ... n)
     */
    public void setIds() {
        int id = 0;
        Vertex depotVertex = null;
        for (Vertex v : getVertices()) {
            if (v.type == VertexType.CUSTOMER) v.id = id++;
            if (v.type == VertexType.DEPOT) depotVertex = v;
        }
        depotVertex.id = id;
    }

    /**
     * adds a vertex to the adjacency customers
     */
    public void addVertex(Vertex u) {
        if (!adjacencyList.containsKey(u.name)) adjacencyList.put(u.name, u);
    }

    /**
     * adds an edge to the adjacency customers
     */
    public void addEdge(Edge e) {
        if (!adjacencyList.containsKey(e.uName)) adjacencyList.put(e.uName, new Vertex(e.uName));
        if (!adjacencyList.containsKey(e.vName)) adjacencyList.put(e.vName, new Vertex(e.vName));
        adjacencyList.get(e.uName).neighbours.put(adjacencyList.get(e.vName), e.weight);
        adjacencyList.get(e.vName).neighbours.put(adjacencyList.get(e.uName), e.weight);
    }

    /**
     * check if the graph contains this vertex name or not
     */
    public boolean containsVertex(String startName) {
        return adjacencyList.containsKey(startName);
    }

    /**
     * gets the graph customersSize
     */
    public int getGraphSize() {
        return adjacencyList.size();
    }

    /**
     * @return adjacencyMatrix of the graph
     */
    public double[][] getAdjacencyMatrix() {
        int numberOfNodes = this.getGraphSize();
        double[][] adjacencyMatrix = new double[numberOfNodes][numberOfNodes];

        for (Vertex u : adjacencyList.values()) {
            for (Vertex v : u.neighbours.keySet()) {
                adjacencyMatrix[u.getId()][v.getId()] = this.getDistance(u, v);
            }
            adjacencyMatrix[u.getId()][u.getId()] = Integer.MAX_VALUE / 2.0;
        }

        return adjacencyMatrix;
    }

    /**
     * prints edges of the graph
     */
    public void printGraph() {
        for (Vertex u : adjacencyList.values()) {
            for (Vertex v : u.neighbours.keySet()) {
                System.out.printf("%s -[%.1f]-> %s\n", u, u.neighbours.get(v), v);
            }
        }
    }

    public String getVerticesFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GlobalVars.equalsLine).append("\n");
        sb.append("v.id\tv.name\tv.type\tv.processTime\tv.dueDate\tv.deadline\tv.penalty\tv.capacity\tv.fixedCost\tv.maxGain").append("\n");
        for (Vertex v : getVertices()) {
            sb.append(String.format("%d\t\t%s\t\t%8s\t%4.1f\t\t\t%4.1f\t\t%4.1f\t\t%4.1f\t\t%4d\t\t%4.1f\t\t%4.1f\n",
                    v.id, v.name, v.type, v.processTime, v.dueDate, v.deadline, v.penalty, v.capacity, v.fixedCost, v.maximumGain));
        }
        sb.append(GlobalVars.equalsLine).append("\n").append("\n");

        return sb.toString();
    }

    /**
     * @return the vertex with given name
     */
    public Vertex getVertexByName(String name) {
        return adjacencyList.get(name);
    }

    /**
     * @return the vertex with given id
     */
    public Vertex getVertexById(int id) {
        if (idToVertexMap == null) {
            idToVertexMap = new HashMap<>();
            for (Vertex v : getVertices()) idToVertexMap.put(v.getId(), v);
        }
        return idToVertexMap.get(id);
    }

    /**
     * @return customers of vertices in the graph
     */
    public Collection<Vertex> getVertices() {
        return adjacencyList.values();
    }

    /**
     * @return customers of customers (customers of neighbors of depot)
     */
    public Collection<Vertex> getCustomerVertices() {
        return getVertexById(getDepotId()).neighbours.keySet();
    }


    public int getDepotId() {
        return getCustomersQty();
    }

    public Vertex getDepot() {
        return getVertexById(getDepotId());
    }

    public int getCustomersQty() {
        return getVertices().size() - 1;
    }

    /**
     * @return distance between to nodes (by name)
     */
    public double getDistance(String uName, String vName) {
        Vertex v = getVertexByName(vName);
        Vertex u = getVertexByName(uName);
        return getDistance(u, v);
    }

    /**
     * @return distance between to nodes (by id)
     */
    public double getDistance(int uId, int vId){
        Vertex v = getVertexById(uId);
        Vertex u = getVertexById(vId);
        return getDistance(u, v);
    }

    /**
     * @return distance between to nodes (by vertex)
     */
    public double getDistance(Vertex u, Vertex v) {
        if (u.getId() == v.getId())
            return 0;
        return u.neighbours.get(v);
    }

    public Graph getCopy() {
        Graph newGraph = new Graph();
        for (Vertex u : this.getVertices()) {
            for (Vertex v : this.getVertices()) {
                newGraph.addVertex(new Vertex(u));
                newGraph.addVertex(new Vertex(v));
                newGraph.addEdge(new Edge(u.name, v.name, getDistance(u, v)));
            }
        }
        return newGraph;
    }
}
