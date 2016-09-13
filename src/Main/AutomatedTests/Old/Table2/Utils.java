package Main.AutomatedTests.Old.Table2;

import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

/**
 * Created by IMN on 8/10/2016.
 */
public class Utils {
    public static void modifyGraphByAutomatedInput(Graph graph, String subSetRow) {
        int nNodes = Integer.parseInt(subSetRow.split(",")[1]);
        int nCustomers = Integer.parseInt(subSetRow.split(",")[2]);
        int nVehicles = Integer.parseInt(subSetRow.split(",")[3]);
        int customers = Integer.parseInt(subSetRow.split(",")[4]);
        int vehicles = Integer.parseInt(subSetRow.split(",")[5]);
        int fixCost = Integer.parseInt(subSetRow.split(",")[6]);
        int penaltyCost = Integer.parseInt(subSetRow.split(",")[7]);

        Vertex depotVertex = graph.getVertexByName("1932");
        depotVertex.penalty = penaltyCost; // depot vertex

        for (int i = 0; i < nNodes; i++) {
            Vertex v = graph.getVertexById(i);
//            System.out.print("Fix Cost " + v.fixedCost);
            v.fixedCost = fixCost;
//            System.out.println(" " + v.fixedCost);

            if ((customers & (1 << i)) != 0)
                v.type = VertexType.CUSTOMER;
            else
                v.type = VertexType.ORDINARY;
        }
    }
}
