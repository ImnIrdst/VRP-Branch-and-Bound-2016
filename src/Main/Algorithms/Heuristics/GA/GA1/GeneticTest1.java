package Main.Algorithms.Heuristics.GA.GA1;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.IOLoader.LoadRandomGraph;

import java.io.FileNotFoundException;

/**
 * Created by IMN on 10/8/2016.
 */
public class GeneticTest1 {
    public static void main(String[] args) throws FileNotFoundException {
        Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(1);

//        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
//                "resources/InputData/ISFNodes-10-Customers.csv",
//                "resources/InputData/ISFRoads.csv"
//        );


        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        preprocessedGraph.setIds();
        GlobalVars.setTheGlobalVariables(preprocessedGraph);

        // print graph
        preprocessedGraph.printVertices();
//        preprocessedGraph.printGraph();

        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles + "\n");
        // run the genetic algorithm

        int geneticTime = 10000;
        int maxIterationsNoUpdate = 1000;
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
        geneticAlgorithm.run(geneticTime, maxIterationsNoUpdate);
        geneticAlgorithm.printBestChromosome();
    }
}
