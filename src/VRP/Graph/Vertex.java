package VRP.Graph;

import java.util.HashMap;
import java.util.Map;

/**
 * simple vertex that has some attributes to use in dijkstra algorithm
 */
public class Vertex {
    public String name; // name of the vertex
    public VertexType type; // Vertex is vehicle or customer
    public Map<Vertex, Integer> neighbours = new HashMap<>(); // an hash map contains neighbors of the nodes, maps the vertex to it's weight (HashMap used for optimal access in O(1))

    // if node is customer
    public int customerId;          // id of the customer for using in branch and bound (filling servicedNodes boolean array)
    public int demand;              // Dc: demand of the customer
    public int penalty;             // Pc: penalty per minute of the customer for being late
    public int dueDate;        // Ec: earliest time for delivery to the customer
    public int serviceTime;         // Sc: time required for a car to service the customer

    // if node is depot
    public int numberOfVehicles; // V: number of vehicles on the depot
    public int fixedCost; // Fv: fixed cost for the vehicle
    public int capacity;  // Qv: capacity for the vehicle

    // these two attributes used for dijkstra algorithm
    public int distOnShortestPath; // distance from source node (in dijkstra)  to the this vertex (MAX_VALUE assumed to be infinity)
    public Vertex previousNodeOnShortestPath;     // previous node in shortest path to this node (in dijkstra)

    // constructors
    public Vertex(String name) {
        this.name = name;
    }

    /**
     * Copy constructor, not copies neighbors and dijkstra attributes
     */
    public Vertex(Vertex vertex) {
        this.name = vertex.name;
        this.type = vertex.type;

        this.customerId = vertex.customerId;
        this.demand = vertex.demand;
        this.penalty = vertex.penalty;
        this.dueDate = vertex.dueDate;
        this.serviceTime = vertex.serviceTime;

        this.numberOfVehicles = vertex.numberOfVehicles;
        this.fixedCost = vertex.fixedCost;
    }

    /**
     * constructor for customers for depot
     */
    public Vertex(String name, VertexType type, int numberOfVehicles, int fixedCost, int capacity) {
        this.name = name;
        this.fixedCost = fixedCost;
        this.capacity = capacity;
        this.numberOfVehicles = numberOfVehicles;
        this.type = type;
    }

    /**
     * constructor for customers
     */
    public Vertex(String name, VertexType type, int customerId,
                  int demand, int penalty, int dueDate, int serviceTime) {
        this.name = name;
        this.type = type;
        this.demand = demand;
        this.penalty = penalty;
        this.dueDate = dueDate;
        this.serviceTime = serviceTime;
        this.customerId = customerId;
    }

    /**
     * constructor for ordinary vertexes
     */
    public Vertex(String name, VertexType type){
        this.name = name;
        this.type = type;
    }

    // prints path recursively in the following format => vertexName(distance from source)
    public void printPath() {
        if (this == this.previousNodeOnShortestPath) {
            System.out.printf("%s(0)", this.name);

        } else if (this.previousNodeOnShortestPath == null) {
            System.out.printf("%s(unreached)", this.name); // there is no path from source to this node

        } else {
            this.previousNodeOnShortestPath.printPath(); // recursive part of the function
            System.out.printf(" -> %s(%d)", this.name, this.distOnShortestPath);

        }
    }

    @Override // hash code used for using vertex in hashMap
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}