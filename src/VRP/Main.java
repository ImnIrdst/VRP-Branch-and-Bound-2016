package VRP;

import VRP.Algorithms.BranchAndBound.BBGlobalVariables;
import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Graph.Graph;

/**
 * for running the algorithm
 */
public class Main {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Graph originalGraph = new Graph("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();
        // preprocessedGraph.printGraph();

        BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph);
        branchAndBound.run("Depot");
        branchAndBound.printTheAnswer();

        long finishTime = System.currentTimeMillis();

        System.out.println();

        System.out.println("Total Calculation time: " + (finishTime - startTime) + "ms");
        System.out.println("Number of Branch and Bound Tree Nodes: " + BBGlobalVariables.numberOfBranchAndBoundNodes);
    }
}