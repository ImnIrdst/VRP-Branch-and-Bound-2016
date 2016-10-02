package Main.Algorithms.SupplyChainScheduling.BranchAndBound;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.IOLoader.LoadRandomGraph;

import java.io.FileNotFoundException;

/**
 * for running the algorithm
 */
public class BBTest {
    public static void main(String[] args) throws FileNotFoundException {
//        Graph originalGraph = LoadRandomGraph.loadWithIntParams(1);
        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/InputData/ISFNodes-10-Customers.csv",
                "resources/InputData/ISFRoads.csv"
        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        preprocessedGraph.setIds();
        GlobalVars.setTheGlobalVariables(preprocessedGraph);
        preprocessedGraph.printVertices();
//        preprocessedGraph.printGraph();

        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles);
        // run the genetic algorithm

        int geneticTime = 100;
//        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
//                preprocessedGraph, GlobalVars.numberOfCustomers, 2, 40);
//        geneticAlgorithm.run(geneticTime);
//        geneticAlgorithm.printBestChromosome();

        // run the branch and bound algorithm
        GlobalVars.startTime = System.currentTimeMillis();
        BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph, 1e9); // geneticAlgorithm.getMinimumCost()
        branchAndBound.run(GlobalVars.depotName);
        GlobalVars.finishTime = System.currentTimeMillis();
        branchAndBound.printTheAnswer();

        // export the result
//         branchAndBound.exportTheResultWTK("/home/iman/Workspace/QGIS/IsfahanVRPResults/", dijkstra);

        // print stats
        System.out.println();
        System.out.println("Total Calculation time: "
                + String.format("%.2f", (geneticTime + GlobalVars.finishTime - GlobalVars.startTime) / 1000.) + "s");
        System.out.println("Number of Branch and Bound Tree Nodes: " + GlobalVars.numberOfBranchAndBoundNodes);
    }
}