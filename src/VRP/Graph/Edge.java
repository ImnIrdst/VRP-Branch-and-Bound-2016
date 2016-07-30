package VRP.Graph;

import java.util.HashMap;

/**
 * Just an ordinary Edge with beginning, end and weight parameters
 */
public class Edge implements Comparable<Edge> {
    public Vertex u;
    public Vertex v;
    public String uName; // beginning vertex name
    public String vName; // ending vertex name
    public final double weight; // weight of the edge (Typically its distance)

    public Edge(String uName, String vName, double weight) {
        this.uName = uName;
        this.vName = vName;
        this.weight = weight;
    }

    public Edge(Vertex u, Vertex v, double weight) {
        this.u = u;
        this.v = v;
        this.weight = weight;
    }

    public static Edge buildEdgeFromAttributeTableRow(String attributeTableRow, HashMap<String, Vertex> coordsToVertexMap) {
        String[] features = attributeTableRow.split(",");

        String OBJECT_ID = features[0];
        String fromX = features[2];
        String fromY = features[3];
        String toX = features[4];
        String toY = features[5];
        String length = features[5];
        String Time = features[6];

        Vertex u = coordsToVertexMap.get(fromX + "," + fromY);
        Vertex v = coordsToVertexMap.get(toX + "," + toY);

        return new Edge(u.name, v.name, Double.parseDouble(Time));
    }

    @Override
    public int compareTo(Edge o) {
        return Double.compare(this.weight, o.weight);
    }
}
