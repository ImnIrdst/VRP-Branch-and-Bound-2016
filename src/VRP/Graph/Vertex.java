package VRP.Graph;

import VRP.GlobalVars;

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
    public int demand;              // Dc: demand of the customer
    public int capacity;            // Q: capacity of vehicle
    public int hasVehicle;          // Binary variable
    public double fixedCost;          // F: fix cost vehicle k
    public double mdt;              // V: Minimum Departure Time
    public double serviceTime;         // Sc: time required for a car to service the customer

    // if node is depot
    public int penalty;
    public double dueDate;

    // these two attributes used for dijkstra algorithm
    public double distOnShortestPath; // distance from source node (in dijkstra)  to the this vertex (MAX_VALUE assumed to be infinity)
    public Vertex previousNodeOnShortestPath;     // previous node in shortest path to this node (in dijkstra)

    // constructors
    public Vertex() {}

    public Vertex(String name) {
        this.name = name;
    }

    /**
     * constructor for depot
     */
    public Vertex(String name, VertexType type, double dueDate, int penalty) {
        this.name = name;
        this.type = type;
        this.dueDate = dueDate;
        this.penalty = penalty;
    }

    /**
     * constructor for customer
     */
    public Vertex(String name, VertexType type,
                  int id, int demand, double serviceTime,
                  int hasVehicle, int capacity, double fixedCost, double mdt) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.demand = demand;
        this.hasVehicle = hasVehicle;
        this.capacity = capacity;
        this.fixedCost = fixedCost;
        this.mdt = mdt;
        this.serviceTime = serviceTime;
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
        this.hasVehicle = vertex.hasVehicle;
        this.demand = vertex.demand;
        this.capacity = vertex.capacity;
        this.fixedCost = vertex.fixedCost;
        this.mdt = vertex.mdt;
        this.serviceTime = vertex.serviceTime;
        this.penalty = vertex.penalty;
        this.dueDate = vertex.dueDate;
    }

    /**
     * build a node from a attribute table row
     */
    public static Vertex buildAVertexFromAttributeTableRow(String attributeTableRow) throws Exception {
        String[] features = attributeTableRow.split(",");

        if (features.length < 12)
            throw new Exception("Reads " + features[0] + " Nodes Successfully!");

        Vertex vertex = new Vertex();

        String OBJECT_ID = features[0];
        String X = features[1];
        String Y = features[2];
        String NodeType = features[3];
        String Demand = features[4];
        String MDT = features[5];
        String ServiceTime = features[6];
        String HasVehicle = features[7];
        String V_FixCost = features[8];
        String V_Cap = features[9];
        String DueDate = features[10];
        String Penalty = features[11];

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

        if (Demand.length() > 0) vertex.demand = Integer.parseInt(Demand);
        if (MDT.length() > 0) vertex.mdt = Double.parseDouble(MDT);
        if (ServiceTime.length() > 0) vertex.serviceTime = Double.parseDouble(ServiceTime);
        if (HasVehicle.length() > 0) vertex.hasVehicle = Integer.parseInt(HasVehicle);
        if (V_FixCost.length() > 0) vertex.fixedCost = Double.parseDouble(V_FixCost);
        if (V_Cap.length() > 0) vertex.capacity = Integer.parseInt(V_Cap);
        if (DueDate.length() > 0) vertex.dueDate = Double.parseDouble(DueDate);
        if (Penalty.length() > 0) vertex.penalty = Integer.parseInt(Penalty);
        if (vertex.type == VertexType.CUSTOMER) vertex.id = GlobalVars.numberOfCustomers;

        // set the global variables
        if (vertex.type == VertexType.DEPOT) GlobalVars.depotName = vertex.name;
        if (vertex.type == VertexType.CUSTOMER) GlobalVars.numberOfCustomers++;
        if (vertex.type == VertexType.CUSTOMER && vertex.hasVehicle == 1) GlobalVars.numberOfVehicles++;

        return vertex;
    }

    /**
     * get Id of the node
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