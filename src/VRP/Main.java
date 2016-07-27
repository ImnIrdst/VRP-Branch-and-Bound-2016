package VRP;

import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Algorithms.Heuristics.GeneticAlgorithm;
import VRP.Graph.Graph;

import java.io.FileNotFoundException;

/**
 * for running the algorithm
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISFNodes-8Customers.csv",
                "resources/ISFRoads.csv"
        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();
//        preprocessedGraph.printGraph();

        // fill the global variables
        GlobalVars.setTheGlobalVariables(preprocessedGraph);

        // run the genetic algorithm
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 50);
        geneticAlgorithm.run(10000);

        // run the branch and bound algorithm
        GlobalVars.startTime = System.currentTimeMillis();
        BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph, geneticAlgorithm.getTheMinimumCost());
        branchAndBound.run(GlobalVars.depotName);
        branchAndBound.printTheAnswer();
        GlobalVars.finishTime = System.currentTimeMillis();

        // export the result
        // branchAndBound.exportTheResultWTK("/home/iman/Workspace/QGIS/IsfahanVRPResults/", dijkstra);

        // print stats
        System.out.println();
        System.out.println("Total Calculation time: "
                + String.format("%.2f", (GlobalVars.finishTime - GlobalVars.startTime)/1000.) + "s");
        System.out.println("Number of Branch and Bound Tree Nodes: " + GlobalVars.numberOfBranchAndBoundNodes);
    }
}