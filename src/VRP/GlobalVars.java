package VRP;

import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

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
    public static long printTimeStepSize = 2000;
    public static double minimumValue;

    // finals
    public static final double INF = 1e9;

    // for reporting
    public static int numberOfBranchAndBoundNodes = 0;


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
        Vertex depotVertex = bbGraph.getVertexById(depotId);
        GlobalVars.depotName = depotVertex.name;

        GlobalVars.numberOfVehicles = 0;
        for (Vertex v : bbGraph.getVertices()) {
            if (v.hasVehicle == 1) GlobalVars.numberOfVehicles++;
        }
    }
}
