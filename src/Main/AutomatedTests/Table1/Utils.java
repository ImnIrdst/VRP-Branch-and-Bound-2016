package Main.AutomatedTests.Table1;

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

        for (int i = 0; i < nNodes; i++) {
            Vertex v = graph.getVertexById(i);

            if ((customers & (1 << i)) != 0)
                v.type = VertexType.CUSTOMER;
            else
                v.type = VertexType.ORDINARY;

//            if ((customers & (1 << i)) != 0 && (vehicles & (1 << i)) != 0)
//                v.hasVehicle = 1;
//            else
//                v.hasVehicle = 0;
        }
    }
}
