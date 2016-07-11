package VRP.Graph;

import java.util.HashMap;
import java.util.Map;

public class Vertex {
    public String name; // name of the vertex
    public VertexType type; // Vertex is vehicle or customer
    public Map<Vertex, Integer> neighbours = new HashMap<>(); // an hash map contains neighbors of the nodes, maps the vertex to it's weight (HashMap used for optimal access in O(1))

    // if node is customer
    public int demand; // Dc: demand of the customer
    public int penalty; // Pc: penalty per minute of the customer for being late
    public int earliestTime; // Ec: earliest time for delivery to the customer
    public int latestTime; // Lc: latest time for delivery to the customer

    // if node is vehicle
    public int fixedCost; // Fv: fixed cost for the vehicle
    public int capacity;  // Qv: capacity for the vehicle

    // these two attributes used for dijkstra algorithm
    public int dist;        // distance from source node (in dijkstra)  to the this vertex (MAX_VALUE assumed to be infinity)
    public Vertex previous; // previous node in shortest path to this node (in dijkstra)

    // constructor
    public Vertex(String name) {
        this.name = name;
    }

    // prints path recursively in the following format => vertexName(distance from source)
    public void printPath() {
        if (this == this.previous) {
            System.out.printf("%s(0)", this.name);

        } else if (this.previous == null) {
            System.out.printf("%s(unreached)", this.name); // there is no path from source to this node

        } else {
            this.previous.printPath(); // recursive part of the function
            System.out.printf(" -> %s(%d)", this.name, this.dist);

        }
    }

    @Override // hash code used for using vertex in hashMap
    public int hashCode() {
        return name.hashCode();
    }
}