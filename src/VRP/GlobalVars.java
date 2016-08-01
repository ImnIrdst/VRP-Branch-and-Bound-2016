package VRP;

import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {

    public static int depotId;
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

    public static MDTPair[] MDT;

    public static Graph bbGraph; // preprocessed graph

    // used for logging
    public static long startTime;
    public static long finishTime;
    public static long bbPrintTime;
    public static long printTimeStepSize = 500;
    public static double minimumValue;

    // finals
    public static final double INF = 1e9;

    // for reporting
    public static int numberOfBranchAndBoundNodes = 1;


    /**
     * initialize the global variables
     *
     * @param bbGraph: the branch and bound graph
     */
    public static void setTheGlobalVariables(Graph bbGraph) throws FileNotFoundException {
        Vertex depotVertex = bbGraph.getVertexByName(depotName);

        GlobalVars.bbGraph = bbGraph;
        GlobalVars.numberOfNodes = bbGraph.getGraphSize();
        GlobalVars.numberOfCustomers = GlobalVars.numberOfNodes - 1;
        GlobalVars.numberOfVehicles = depotVertex.numberOfVehicles;
        GlobalVars.vehicleCapacity = depotVertex.capacity;
        GlobalVars.vehicleFixedCost = depotVertex.fixedCost;
        GlobalVars.depotId = depotVertex.getId();

        // due dates, demands, penalties, service times
        GlobalVars.nodeDueDates = new double[numberOfNodes];
        GlobalVars.customerDemands = new int[numberOfNodes];
        GlobalVars.customerPenaltyCosts = new int[numberOfNodes];
        GlobalVars.customerServiceTimes = new double[numberOfNodes];
        for (Vertex u : bbGraph.getVertices()) {
            GlobalVars.nodeDueDates[u.getId()] = u.dueDate;
            GlobalVars.customerDemands[u.getId()] = u.demand;
            GlobalVars.customerPenaltyCosts[u.getId()] = u.penalty;
            GlobalVars.customerServiceTimes[u.getId()] = u.serviceTime;
        }

        // vehicle capacities
        GlobalVars.vehicleCapacities = new int[GlobalVars.numberOfVehicles];
        for (int i = 0; i < GlobalVars.numberOfVehicles; i++) {
            GlobalVars.vehicleCapacities[i] = GlobalVars.vehicleCapacity;
        }

        // MDTs
        GlobalVars.MDT = new MDTPair[GlobalVars.numberOfVehicles];
        FileInputStream file = new FileInputStream(new File("resources/vehiclesMDT.txt"));
        Scanner sc = new Scanner(file);
        String[] mdtStrings = sc.nextLine().split(",");
        for (int i=0 ; i<GlobalVars.numberOfVehicles ; i++){
            GlobalVars.MDT[i] = new MDTPair(i, Double.parseDouble(mdtStrings[i]));
        }
        Arrays.sort(GlobalVars.MDT);
    }

    public static class MDTPair implements Comparable<MDTPair> {
        public int id;
        public double mdt;

        public MDTPair(int id, double mdt) {
            this.id = id;
            this.mdt = mdt;
        }

        @Override
        public int compareTo(MDTPair o) {
            return Double.compare(this.mdt, o.mdt);
        }
    }
}
