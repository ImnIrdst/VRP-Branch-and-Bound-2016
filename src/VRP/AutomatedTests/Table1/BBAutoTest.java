package VRP.AutomatedTests.Table1;

import VRP.Algorithms.BranchAndBound.BranchAndBound;
import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.Algorithms.Heuristics.GeneticAlgorithm;
import VRP.GlobalVars;
import VRP.Graph.Graph;

import java.io.*;
import java.util.Scanner;

/**
 * for running the algorithm
 */
public class BBAutoTest {
    public static void main(String[] args) throws FileNotFoundException {

        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISFNodes-10-09-Ex2.csv",
                "resources/ISFRoads.csv"
        );
//        Graph originalGraph = Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph reducedGraph = dijkstra.makeShortestPathGraph();
        reducedGraph.setIds();

        FileInputStream fileInputStream = new FileInputStream(new File("resources/t1-automated-test-results-cplex-01.csv"));
        Scanner sc = new Scanner(fileInputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/t1-automated-test-results-cplex-bb-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);
        out.println(sc.nextLine() + ",BBValue,BBTime,BBNodes");
        out.flush();

        while (sc.hasNextLine()) {
            String autoTestRow = sc.nextLine();
//            if (!autoTestRow.split(",")[9].equals("Optimal")){
//                out.println(autoTestRow); continue;
//            }
//            if (!Objects.equals(autoTestRow.split(",")[0], "203")) continue;
            Graph preprocessedGraph = reducedGraph.getCopy();
            Utils.modifyGraphByAutomatedInput(preprocessedGraph, autoTestRow);

            Dijkstra dijkstra2 = new Dijkstra(preprocessedGraph);
            preprocessedGraph = dijkstra2.makeShortestPathGraph();

            // fill the global variables
            preprocessedGraph.setIds();
            GlobalVars.setTheGlobalVariables(preprocessedGraph);
//            preprocessedGraph.printVertices();
//            preprocessedGraph.printGraph();

            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + "-1");

            int geneticTime = 0;
//            if (GlobalVars.numberOfCustomers == 11) geneticTime = 1000;
            if (GlobalVars.numberOfCustomers == 12) geneticTime = 10000;

            // run the genetic algorithm
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                    preprocessedGraph, GlobalVars.numberOfCustomers, 2, 40);
            geneticAlgorithm.run(geneticTime);
//            geneticAlgorithm.printBestChromosome();


            String expandedNodes = "";
            String elapsedTime = "";
            String optimalValue = "";

            GlobalVars.startTime = System.currentTimeMillis();
            try {
                // run the branch and bound algorithm
                BranchAndBound branchAndBound = new BranchAndBound(preprocessedGraph, geneticAlgorithm.getMinimumCost() + 1e-9); // geneticAlgorithm.getMinimumCost()
                branchAndBound.run(GlobalVars.depotName);
                GlobalVars.finishTime = System.currentTimeMillis();
                System.out.printf("Optimal Cost: %.2f\n", branchAndBound.bestNode.getCost());

                optimalValue = String.format("%.2f", branchAndBound.minimumCost);
            } catch (NullPointerException e) {
                optimalValue = "NA";
            } catch (OutOfMemoryError e) {
                optimalValue = "ML";
            }
            GlobalVars.finishTime = System.currentTimeMillis();
            expandedNodes = "" + GlobalVars.numberOfBranchAndBoundNodes;
            elapsedTime = String.format("%.2f", (geneticTime + GlobalVars.finishTime - GlobalVars.startTime) / 1000.);

            System.out.println(autoTestRow);
            System.out.println("Optimal Value: " + optimalValue);
            System.out.println("Total Calculation time: " + elapsedTime + "s");
            System.out.println("Number of Branch and Bound Tree Nodes: " + expandedNodes);
            System.out.println("-------------------------------------");
            out.println(autoTestRow + "," + optimalValue + "," + elapsedTime + "," + expandedNodes);
            out.flush();
        }
        out.close();
    }
}