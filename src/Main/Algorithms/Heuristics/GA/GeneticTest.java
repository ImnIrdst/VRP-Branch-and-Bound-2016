package Main.Algorithms.Heuristics.GA;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestGenerator;
import Main.GlobalVars;
import Main.Graph.Graph;

import java.io.FileNotFoundException;

/**
 * Created by IMN on 10/8/2016.
 */
public class GeneticTest {
    public static void main(String[] args) throws FileNotFoundException {
        SCSTestGenerator scsTestGenerator = new SCSTestGenerator();
        Graph originalGraph = Graph.buildRandomGraphFromIntegerTestCase(scsTestGenerator.getNextTestCase(), 0);

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
        preprocessedGraph.getVerticesFormattedString();
//        preprocessedGraph.printGraph();

        System.out.println("Number of Customers: " + GlobalVars.numberOfCustomers);
        System.out.println("Number of Vehicles: " + GlobalVars.numberOfVehicles + "\n");
        // run the genetic algorithm

        int geneticTime = 10000;
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
        geneticAlgorithm.run(geneticTime, 1000 , 2000);
        geneticAlgorithm.bestChromosomeString();
    }
}