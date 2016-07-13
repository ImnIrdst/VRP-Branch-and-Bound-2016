package VRP;

import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Graph.Graph;

/**
 * for running the algorithm
 */
public class Main {
    public static void main(String[] args){
        Graph originalGraph = new Graph("resources/input.csv");

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();
        // preprocessedGraph.printGraph();

        BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph);
        branchAndBound.run("Depot");
        branchAndBound.printTheAnswer();
    }
}