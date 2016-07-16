package VRP.Algorithms.BranchAndBound;

import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.Map;

/**
 * Some Utilities for Branch and Bound algorithm
 */
public class BBUtils {
    /**
     * returns distance between to node (for simplicity)
     * @param u beginning node
     * @param v end node
     * @return distance of the two nodes
     */
    public static int getDistance(Vertex u, Vertex v) {
        if (u.type == VertexType.DEPOT && v.type == VertexType.DEPOT) {
            return Integer.MAX_VALUE; // there is no way from
        } else {
            return BBGlobalVariables.graph.adjacencyList.get(u.name).neighbours.get(v);
        }
    }
}
