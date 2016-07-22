package VRP;

import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Graph.Graph;

/**
 * for running the algorithm
 */
public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFNodes.csv",
                "/home/iman/Workspace/QGIS/IsfahanAttributeTables/ISFRoads.csv"
        );
        // Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        preprocessedGraph.printGraph();
        BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph);
        branchAndBound.run(GlobalVars.depotName);
        branchAndBound.printTheAnswer();

        long finishTime = System.currentTimeMillis();

        System.out.println();

        System.out.println("Total Calculation time: " + (finishTime - startTime) + "ms");
        System.out.println("Number of Branch and Bound Tree Nodes: " + GlobalVars.numberOfBranchAndBoundNodes);
    }
}