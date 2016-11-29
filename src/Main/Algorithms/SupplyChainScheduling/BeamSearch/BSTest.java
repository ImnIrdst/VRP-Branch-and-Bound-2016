package Main.Algorithms.SupplyChainScheduling.BeamSearch;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Algorithms.Heuristics.GA.GA1.GeneticAlgorithm;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.IOLoader.LoadRandomGraph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * for running the algorithm
 */
public class BSTest {
    public static void main(String[] args) throws FileNotFoundException {
        Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(57);
//        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
//                "resources/InputData/ISFNodes-10-Customers.csv",
//                "resources/InputData/ISFRoads.csv"
//        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        preprocessedGraph.setIds();
        GlobalVars.setTheGlobalVariables(preprocessedGraph);
        preprocessedGraph.getVerticesFormattedString();
//        preprocessedGraph.printGraph();

        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles);
        // run the genetic algorithm
        GlobalVars.log = new PrintWriter(System.out);

        int geneticTime = 0;
        int maxIterationNoUpdate = 0;
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
        geneticAlgorithm.run(geneticTime, 0, 0);
        geneticAlgorithm.printBestChromosome();

        // run the branch and bound algorithm
        double theta0 = Math.min(1.0, 1.1 - (GlobalVars.numberOfCustomers / 100.0));
        GlobalVars.startTime = System.currentTimeMillis();
        BeamSearch beamSearch = new BeamSearch(preprocessedGraph, 1, geneticAlgorithm.getMinimumCost()); // geneticAlgorithm.getMinimumCost()
        beamSearch.run(GlobalVars.depotName);
        GlobalVars.finishTime = System.currentTimeMillis();
        beamSearch.printTheAnswer();
        System.out.println(beamSearch.getHierarchy(beamSearch.bestNode));

        // export the result
//         beamSearch.exportTheResultWTK("/home/iman/Workspace/QGIS/IsfahanVRPResults/", dijkstra);

        // print stats
        System.out.println();
        System.out.println("Total Calculation time: "
                + String.format("%.2f", (geneticTime + GlobalVars.finishTime - GlobalVars.startTime) / 1000.) + "s");
        System.out.println("Number of Branch and Bound Tree Nodes: " + GlobalVars.numberOfBranchAndBoundNodes);

        System.out.println(GlobalVars.equalsLine);
        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles);
    }
}