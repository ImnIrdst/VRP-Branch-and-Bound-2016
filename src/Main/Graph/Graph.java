package Main.Graph;

import Main.Algorithms.Other.Random;
import Main.Algorithms.Other.Random.IRange;
import Main.Algorithms.Other.Random.DRange;
import Main.AutomatedTests.TestCases.SCSTestCase;
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

//    /**
//     * builds a Main.Graph from a csv file
//     *
//     * @param path: path to the csv file
//     */
//    public static Main.Graph buildAGraphFromCSVFile(String path) {
//        Main.Graph graph = new Main.Graph();
//        try {
//            // read file
//            FileInputStream file = new FileInputStream(new File(path));
//            Scanner sc = new Scanner(file);
//            sc.nextLine();
//            sc.nextLine();
//
//            String[] tokens;
//
//            // read depot Info
//            tokens = sc.nextLine().split(",");
//            graph.addVertex(new Vertex("Depot", VertexType.DEPOT,
//                    Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
//
//
//            // read customers info
//            int numberOfCustomers = Integer.parseInt(sc.nextLine().split(",")[1]);
//            sc.nextLine(); // skip the line
//
//            for (int i = 0; i < numberOfCustomers; i++) {
//                tokens = sc.nextLine().split(",");
//                Vertex newVertex = new Vertex(tokens[0],
//                        VertexType.CUSTOMER,
//                        Integer.parseInt(tokens[1]),
//                        Double.parseDouble(tokens[2]),
//                        Integer.parseInt(tokens[3]),
//                        Integer.parseInt(tokens[4]),
//                        Integer.parseInt(tokens[5]),
//                        Double.parseDouble(tokens[6]));
//                graph.addVertex(newVertex);
//            }
//
//            // read ordinary vertices
//            int numberOfOrdinaryVertices = Integer.parseInt(sc.nextLine().split(",")[1]);
//            sc.nextLine(); // skip the line
//
//            for (int i = 0; i < numberOfOrdinaryVertices; i++) {
//                graph.addVertex(new Vertex(sc.nextLine(), VertexType.ORDINARY));
//            }
//
//            // read edges
//            int numberOfEdges = Integer.parseInt(sc.nextLine().split(",")[1]);
//            sc.nextLine(); // skip the line
//
//            for (int i = 0; i < numberOfEdges; i++) {
//                tokens = sc.nextLine().split(",");
//                graph.addEdge(new Edge(tokens[0], tokens[1], Integer.parseInt(tokens[2])));
//            }
//
//            return graph;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

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
     * build a Main.Graph from an attribute table
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // read roads
        try {
            FileInputStream file = new FileInputStream(new File(roadsFilePath));
            Scanner sc = new Scanner(file);
            sc.nextLine();

            while (sc.hasNextLine()) {
                Edge edge = Edge.buildEdgeFromAttributeTableRow(sc.nextLine(), coordsToVertexMap);
                if (edge != null) graph.addEdge(edge);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return graph;
    }

    public static Graph buildRandomGraphDouble(
            IRange customerQtyRange, IRange vehicleQtyRange, IRange capacityRange, DRange fixCostRange,
            DRange processTimeRange, DRange dueDateRange, DRange penaltyRange, DRange edgeWeightsRange) {
        Graph graph = new Graph();
        // Add Depot Vertex
        int customerQty = Random.getRandomIntInRange(customerQtyRange);
        vehicleQtyRange.max = Math.min(vehicleQtyRange.max, customerQty);
        vehicleQtyRange.min = Math.min(vehicleQtyRange.min, vehicleQtyRange.max);
        int vehicleQty = Random.getRandomIntInRange(vehicleQtyRange);
        int capacity = Random.getRandomIntInRange(capacityRange);
        double fixCost = Random.getRandomDoubleInRange(fixCostRange);

        while (customerQty > vehicleQty * capacity) {
            vehicleQty = Random.getRandomIntInRange(vehicleQtyRange);
            capacity = Random.getRandomIntInRange(capacityRange);
        }

        graph.addVertex(new Vertex("Dp", VertexType.DEPOT, vehicleQty, capacity, fixCost));

        // Build the Customer Vertices
        for (int i = 0; i < customerQty; i++) {
            String name = "" + (char) ('A' + i);
            double processTime = Random.getRandomDoubleInRange(processTimeRange);
            double dueDate = Random.getRandomDoubleInRange(dueDateRange) + processTime + edgeWeightsRange.min;
            double penalty = Random.getRandomDoubleInRange(penaltyRange);
            graph.addVertex(new Vertex(name, VertexType.CUSTOMER, processTime, dueDate, penalty));
        }

        // Add The Edges
        for (Vertex u : graph.getVertices()) {
            for (Vertex v : graph.getVertices()) {
                if (u.name.equals(v.name)) continue;
                Edge e = new Edge(u, v, Random.getRandomDoubleInRange(edgeWeightsRange));
                graph.addEdge(e);
            }
        }

        return graph;
    }

    public static Graph buildRandomGraphInt(
            IRange customerQtyRange, IRange vehicleQtyRange, IRange capacityRange, IRange fixCostRange,
            IRange processTimeRange, IRange dueDateRange, IRange penaltyRange, IRange edgeWeightsRange) {
        Graph graph = new Graph();
        // Add Depot Vertex
        int vehicleQty = Random.getRandomIntInRange(vehicleQtyRange);
        int capacity = Random.getRandomIntInRange(capacityRange);
        double fixCost = Random.getRandomIntInRange(fixCostRange);
        graph.addVertex(new Vertex("Dp", VertexType.DEPOT, vehicleQty, capacity, fixCost));

        // Build the Customer Vertices
        int customerQty = Random.getRandomIntInRange(customerQtyRange);
        for (int i = 0; i < customerQty; i++) {
            String name = "" + (char) ('A' + i);
            double processTime = Random.getRandomIntInRange(processTimeRange);
            double dueDate = Random.getRandomIntInRange(dueDateRange);
            double penalty = Random.getRandomIntInRange(penaltyRange);
            graph.addVertex(new Vertex(name, VertexType.CUSTOMER, processTime, dueDate, penalty));
        }

        // Add The Edges
        for (Vertex u : graph.getVertices()) {
            for (Vertex v : graph.getVertices()) {
                if (u.name.equals(v.name)) continue;
                Edge e = new Edge(u, v, Random.getRandomIntInRange(edgeWeightsRange));
                graph.addEdge(e);
            }
        }

        return graph;
    }

    public static Graph buildRandomGraphFromTestCase(SCSTestCase testCase, int seed) {
        Random.setSeed(seed);

        Graph graph = new Graph();
        // Add Depot Vertex
        int capacity = (int) (testCase.customerQty * Random.getRandomDoubleInRange(testCase.capacityRange));
        graph.addVertex(new Vertex("Dp", VertexType.DEPOT, testCase.vehicleQty, capacity, testCase.fixCost));

        // Build the Customer Vertices
        double sumOfProcessTimes = 0;
        for (int i = 0; i < testCase.customerQty; i++) {
            String name = "" + (char) ('A' + i);
            double processTime = Random.getRandomDoubleInRange(testCase.processTimeRange);
            double dueDate = Random.getRandomDoubleInRange(testCase.dueDateRange);
            double penalty = Random.getRandomDoubleInRange(testCase.penaltyRange);
            graph.addVertex(new Vertex(name, VertexType.CUSTOMER, processTime, dueDate, penalty));

            sumOfProcessTimes += processTime;
        }

        // Add The Edges
        for (Vertex u : graph.getVertices()) {
            u.dueDate *= sumOfProcessTimes; // (0.4*sumOfProcessTimes - 0.7*sumOfProcessTimes)
            for (Vertex v : graph.getVertices()) {
                if (u.name.equals(v.name)) continue;
                Edge e = new Edge(u, v, Random.getRandomDoubleInRange(testCase.edgeWeightRange));
                graph.addEdge(e);
            }
        }

        return graph;
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
        sb.append("v.id\tv.name\tv.type\tv.processTime\tv.dueDate\tv.penalty\tv.capacity\tv.fixedCost").append("\n");
        for (Vertex v : getVertices()) {
            sb.append(String.format("%d\t\t%s\t\t%8s\t%4.1f\t\t\t%4.1f\t\t%4.1f\t\t%4d\t\t%4.1f\n",
                    v.id, v.name, v.type, v.processTime, v.dueDate, v.penalty, v.capacity, v.fixedCost));
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

    /**
     * @return a wtk string for the edge between to nodes in the graph
     */
    public String getEdgeWTK(String uName, String vName) {
        Vertex u = getVertexByName(uName);
        Vertex v = getVertexByName(vName);

        return "LINESTRING(" + u.getSpacedCoords() + ", " + v.getSpacedCoords() + ")";
    }
}
