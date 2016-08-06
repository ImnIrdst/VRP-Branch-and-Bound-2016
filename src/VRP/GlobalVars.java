package VRP;

import VRP.Graph.Graph;
import VRP.Graph.Vertex;

import java.io.FileNotFoundException;

/**
 * global variables used in branch and bound
 */
public class GlobalVars {

    public static int depotId;
    public static String depotName;

    public static int numberOfVehicles = 0;
    public static int numberOfNodes = 0;
    public static int numberOfCustomers = 0;

    public static Graph ppGraph; // preprocessed graph

    // used for logging
    public static long startTime;
    public static long finishTime;
    public static long bbPrintTime;
    public static long printTimeStepSize = 1000;
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

        GlobalVars.ppGraph = bbGraph;
        GlobalVars.numberOfNodes = bbGraph.getGraphSize();
        GlobalVars.numberOfCustomers = GlobalVars.numberOfNodes - 1;
        depotVertex.id = GlobalVars.numberOfCustomers;
        GlobalVars.depotId = depotVertex.getId();

    }
}
