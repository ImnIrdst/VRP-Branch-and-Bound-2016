package VRP.Graph;

import java.util.HashMap;

/**
 * Just an ordinary Edge with beginning, end and weight parameters
 */
public class Edge {
    public final String u; // beginning vertex name
    public final String v; // ending vertex name
    public final double weight; // weight of the edge (Typically its distance)

    public Edge(String u, String v, double weight) {
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
}
