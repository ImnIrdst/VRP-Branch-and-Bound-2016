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
    public Map<Integer, Vertex> idToVertexMap; // maps vertex Id to Vertex

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
            sc.nextLine();
            sc.nextLine();

            String[] tokens;

            // read depot Info
            tokens = sc.nextLine().split(",");
            GlobalVars.depotName = "Depot"; // fill the global variables
            graph.addVertex(new Vertex(GlobalVars.depotName, VertexType.DEPOT,
                    Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));


            // read customers info
            int numberOfCustomers = Integer.parseInt(sc.nextLine().split(",")[1]);
            sc.nextLine(); // skip the line

            int cId = 0;
            GlobalVars.numberOfVehicles = 0;
            for (int i = 0; i < numberOfCustomers; i++) {
                tokens = sc.nextLine().split(",");
                Vertex newVertex = new Vertex(tokens[0],
                        VertexType.CUSTOMER, cId++,
                        Integer.parseInt(tokens[1]),
                        Double.parseDouble(tokens[2]),
                        Integer.parseInt(tokens[3]),
                        Integer.parseInt(tokens[4]),
                        Integer.parseInt(tokens[5]),
                        Double.parseDouble(tokens[6]));
                if (newVertex.hasVehicle == 1)
                    GlobalVars.numberOfVehicles++;
                graph.addVertex(newVertex);
            }

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
                tokens = sc.nextLine().split(",");
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
     * gets the graph size
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
                System.out.println(u + " -(" + u.neighbours.get(v) + ")-> " + v);
            }
        }
    }

    public void printVertices(){
        System.out.println("v.demand, v.hasVehicle, v.capacity, v.fixedCost");
        for (Vertex v: getVertices()){
            System.out.printf("%d\t%d\t%d\t%.2f\n", v.demand, v.hasVehicle, v.capacity, v.fixedCost);
        }
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
     * @return list of vertices in the graph
     */
    public Collection<Vertex> getVertices() {
        return adjacencyList.values();
    }

    /**
     * @return list of customers (list of neighbors of depot)
     */
    public Collection<Vertex> getCustomerVertices() {
        return getVertexById(GlobalVars.depotId).neighbours.keySet();
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
