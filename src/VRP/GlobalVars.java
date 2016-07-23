package VRP;

import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {
    public static String depotName;

    public static int numberOfNodes = 0;
    public static int numberOfCustomers = 0;
    public static int numberOfVehicles = 0;
    public static int vehicleCapacity = 0;
    public static double vehicleFixedCost = 0;

    public static int[] vehicleCapacities;
    public static int[] customerDemands;
    public static int[] customerPenaltyCosts;
    public static double[] customerServiceTimes;
    public static double[] nodeDueDates;


    public static Graph bbGraph; // preprocessed graph

    /**
     * initialize the global variables
     *
     * @param bbGraph: the branch and bound graph
     */
    public static void setTheGlobalVariables(Graph bbGraph) {
        Vertex depotVertex = bbGraph.getVertexByName(depotName);

        GlobalVars.bbGraph = bbGraph;
        GlobalVars.numberOfNodes = bbGraph.getGraphSize();
        GlobalVars.numberOfCustomers = GlobalVars.numberOfNodes - 1;
        GlobalVars.numberOfVehicles = depotVertex.numberOfVehicles;
        GlobalVars.vehicleCapacity = depotVertex.capacity;
        GlobalVars.vehicleFixedCost = depotVertex.fixedCost;

        // due dates
        GlobalVars.nodeDueDates = new double[numberOfNodes];
        for (Vertex u : bbGraph.getVertices()) {
            GlobalVars.nodeDueDates[u.getId()] = u.dueDate;
        }

        // other customer features
        GlobalVars.customerDemands = new int[numberOfCustomers];
        GlobalVars.customerPenaltyCosts = new int[numberOfCustomers];
        GlobalVars.customerServiceTimes = new double[numberOfCustomers];
        for (Vertex u : bbGraph.getVertices()) {
            if (u.type != VertexType.CUSTOMER) continue;
            GlobalVars.customerDemands[u.getId()] = u.demand;
            GlobalVars.customerPenaltyCosts[u.getId()] = u.penalty;
            GlobalVars.customerServiceTimes[u.getId()] = u.serviceTime;
        }

        // vehicle capacities
        GlobalVars.vehicleCapacities = new int[GlobalVars.numberOfVehicles];
        for (int i = 0; i < GlobalVars.numberOfVehicles; i++) {
            GlobalVars.vehicleCapacities[i] = GlobalVars.vehicleCapacity;
        }
    }

    // for reporting
    public static int numberOfBranchAndBoundNodes = 0;
}
