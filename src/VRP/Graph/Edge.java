package VRP.Graph;

/**
 * Just an ordinary Edge with beginning, end and weight parameters
 */
public class Edge {
    public final String u; // beginning vertex name
    public final String v; // ending vertex name
    public final int weight; // weight of the edge (Typically its distance)

    public Edge(String u, String v, int weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }
}
