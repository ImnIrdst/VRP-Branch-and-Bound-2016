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

    public static int numberOfVehicles = 0;
    public static int numberOfNodes = 0;
    public static int numberOfCustomers = 0;

    public static int[] customerDemands;

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
        GlobalVars.depotId = depotVertex.getId();
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
