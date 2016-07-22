package VRP;

import VRP.Graph.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {
    public static String depotName = "Depot";

    public static int numberOfVehicles = 0;
    public static int vehicleCapacity = 0;
    public static double vehicleFixedCost = 0;

    public static int numberOfCustomers = 0;
    public static List<Integer> customerDemands = new ArrayList<>();

    public static Graph bbGraph; // preprocessed graph

    // for reporting
    public static int numberOfBranchAndBoundNodes = 0;
}
