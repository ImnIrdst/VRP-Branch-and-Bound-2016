package VRP.Algorithms.BeamSearch;

import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Algorithms.Heuristics.GeneticAlgorithm;
import VRP.GlobalVars;
import VRP.Graph.Graph;

import java.io.FileNotFoundException;

/**
 * for running the algorithm
 */
public class BSMain {
    public static void main(String[] args) throws FileNotFoundException {

        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "C:\\Users\\IMN\\Desktop\\Projects\\VRP-Branch-and-Bound-2016\\resources\\Old\\ISF-20-Customers.csv",
                "resources/ISFRoads.csv"
        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        preprocessedGraph.setIds();
        GlobalVars.setTheGlobalVariables(preprocessedGraph);
//        preprocessedGraph.printVertices();
//        preprocessedGraph.printGraph();

        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles);
        // run the genetic algorithm

        int geneticTime = 0;
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 40);
        geneticAlgorithm.run(geneticTime);
        geneticAlgorithm.printBestChromosome();

        // run the branch and bound algorithm
        GlobalVars.startTime = System.currentTimeMillis();
        BeamSearch beamSearch = new BeamSearch(preprocessedGraph, GlobalVars.INF); // geneticAlgorithm.getMinimumCost()
        beamSearch.run(GlobalVars.depotName);
        GlobalVars.finishTime = System.currentTimeMillis();
        beamSearch.printTheAnswer();
        
        // export the result
        // beamSearch.exportTheResultWTK("/home/iman/Workspace/QGIS/IsfahanVRPResults/", dijkstra);

        // print stats
        System.out.println();
        System.out.println("Total Calculation time: "
                + String.format("%.2f", (geneticTime + GlobalVars.finishTime - GlobalVars.startTime)/1000.) + "s");
        System.out.println("Number of Branch and Bound Tree Nodes: " + GlobalVars.numberOfBranchAndBoundNodes);
    }
}