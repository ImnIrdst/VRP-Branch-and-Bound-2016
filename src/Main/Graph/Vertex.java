package Main.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * simple vertex that has some attributes to use in dijkstra algorithm
 */
public class Vertex {
    public String name; // name of the vertex
    public String coords; // coordinates of the vertex as comma separated a string
    public VertexType type; // Vertex is vehicle or customer
    public Map<Vertex, Double> neighbours = new HashMap<>(); // an hash map contains neighbors of the nodes, maps the vertex to it's weight (HashMap used for optimal access in O(1))

    // if node is customer
    public int id;          // id of the customer for using in branch and bound (filling servicedNodes boolean array)
    public double penalty;
    public double dueDate;
    public double deadline;
    public double processTime;
    public double maximumGain;

    // if node is depot
    public int vehicleQty;          // number of customersVehicle
    public int capacity;            // Q: capacity of vehicle
    public double fixedCost;          // F: fix cost vehicle k


    // these two attributes used for dijkstra algorithm
    public double distOnShortestPath; // distance from source node (in dijkstra)  to the this vertex (MAX_VALUE assumed to be infinity)
    public Vertex previousNodeOnShortestPath;     // previous node in shortest path to this node (in dijkstra)


    // constructors
    public Vertex() {}

    public Vertex(String name) {
        this.name = name;
    }

    /**
     * constructor for customers
     */
    public Vertex(String name, VertexType type, double processTime, double dueDate, double deadline, double penalty, double maximumGain) {
        this.name = name;
        this.type = type;
        this.dueDate = dueDate;
        this.deadline = deadline;
        this.penalty = penalty;
        this.processTime = processTime;
        this.maximumGain = maximumGain;
    }

    /**
     * constructor for depot
     */
    public Vertex(String name, VertexType type, int vehicleQty, int capacity, double fixedCost) {
        this.name = name;
        this.type = type;
        this.vehicleQty = vehicleQty;
        this.capacity = capacity;
        this.fixedCost = fixedCost;
    }

    /**
     * constructor for ordinary vertexes
     */
    public Vertex(String name, VertexType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Copy constructor, not copies neighbors and dijkstra attributes
     */
    public Vertex(Vertex vertex) {
        this.name = vertex.name;
        this.coords = vertex.coords;
        this.type = vertex.type;
        this.id = vertex.id;
        this.capacity = vertex.capacity;
        this.fixedCost = vertex.fixedCost;
        this.penalty = vertex.penalty;
        this.dueDate = vertex.dueDate;
        this.vehicleQty = vertex.vehicleQty;
        this.processTime = vertex.processTime;
    }

    /**
     * build a node from a attribute table row
     */
    public static Vertex buildAVertexFromAttributeTableRow(String attributeTableRow) throws Exception {
        String[] features = attributeTableRow.split(",");

        if (features.length < 11)
            throw new Exception("Reads " + features[0] + " Nodes Successfully!");

        Vertex vertex = new Vertex();

        String OBJECT_ID = features[0];
        String X = features[1];
        String Y = features[2];
        String NodeType = features[3];
        String V_Qty = features[4];
        String V_FixCost = features[5];
        String V_Cap = features[6];
        String P_Time = features[7];
        String DueDate = features[8];
        String Penalty = features[9];

        vertex.name = OBJECT_ID;
        vertex.coords = X + "," + Y;

        switch (NodeType) {
            case "Depot":
                vertex.type = VertexType.DEPOT;
                break;
            case "Customer":
                vertex.type = VertexType.CUSTOMER;
                break;
            default:
                vertex.type = VertexType.ORDINARY;
                break;
        }

        if (V_Qty.length() > 0 ) vertex.vehicleQty = Integer.parseInt(V_Qty);
        if (V_FixCost.length() > 0) vertex.fixedCost = Double.parseDouble(V_FixCost);
        if (V_Cap.length() > 0) vertex.capacity = Integer.parseInt(V_Cap);
        if (DueDate.length() > 0) vertex.dueDate = Double.parseDouble(DueDate);
        if (Penalty.length() > 0) vertex.penalty = Double.parseDouble(Penalty);
        if (P_Time.length() > 0) vertex.processTime = Double.parseDouble(P_Time);

        return vertex;
    }

    /**
     * getCustomer Id of the node
     * if n is number of nodes then
     * 1,2,...,n-1 -> for customers
     * n -> depot
     * only must be used for Branch and Bound and after graph has been created.
     */
    public int getId() {
        if (this.type == VertexType.CUSTOMER || this.type == VertexType.DEPOT)
            return this.id;
        return -1;
    }

    /**
     * @return coordinates with space between (for wtk)
     */
    public String getSpacedCoords() {
        String X = coords.split(",")[0];
        String Y = coords.split(",")[1];
        return X + " " + Y;
    }

    /**
     * prints path recursively in the following format => vertexName(distance from source)
     */
    public String getPrintPathString() {
        if (this == this.previousNodeOnShortestPath) {
            return this.name + " (" + this.distOnShortestPath + ")";

        } else if (this.previousNodeOnShortestPath == null) {
            return this.name + " (unreached)"; // there is no path from source to this node

        } else {
            return this.previousNodeOnShortestPath.getPrintPathString()
                    + " -> " + this.name + " (" + this.distOnShortestPath + ")"; // recursive part of the function
        }
    }

    @Override // hash code used for using vertex in hashMap
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + "[" + getId() + "]";
    }


}