package VRP;

import VRP.Graph.Graph;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {
    public static String depotName = "Depot";

    public static int vehicleFixedCost = 0;
    public static int vehicleCapacity = 0;
    public static int numberOfCustomers = 0;
    public static int numberOfVehicles = 0;

    public static Graph graph;
    public static Integer[] customerDemands;

    // for reporting
    public static int numberOfBranchAndBoundNodes = 0;
}
