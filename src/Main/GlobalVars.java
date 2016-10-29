package Main;

import Main.Graph.Graph;
import Main.Graph.Vertex;

import java.io.FileNotFoundException;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {

    public static int depotId;
    public static String depotName;
    public static Vertex depot;

    public static int numberOfNodes;
    public static int numberOfCustomers;
    public static int numberOfVehicles;

    public static Graph ppGraph; // preprocessed graph

    // used for logging
    public static long startTime;
    public static long finishTime;
    public static long bbPrintTime;
    public static long printTimeStepSize = 2000;
    public static double minimumValue;

    // finals
    public static final double INF = 1e8;

    // for reporting
    public static int numberOfBranchAndBoundNodes = 0;
    public static boolean enableBreakPoints = false;

    public static String equalsLine =
            "============================================================================================";
    public static String dashesLine =
            "--------------------------------------------------------------------------------------------";
    public static String plusesLine =
            "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++";

    /**
     * initialize the global variables
     *
     * @param bbGraph: the branch and bound graph
     */
    public static void setTheGlobalVariables(Graph bbGraph) throws FileNotFoundException {
        GlobalVars.numberOfBranchAndBoundNodes = 0;

        GlobalVars.ppGraph = bbGraph;
        GlobalVars.numberOfNodes = bbGraph.getGraphSize();
        GlobalVars.numberOfCustomers = GlobalVars.numberOfNodes - 1;
        GlobalVars.depotId = GlobalVars.numberOfCustomers;
        depot = bbGraph.getVertexById(depotId);
        GlobalVars.depotName = depot.name;
        GlobalVars.numberOfVehicles = depot.vehicleQty;
    }
}
