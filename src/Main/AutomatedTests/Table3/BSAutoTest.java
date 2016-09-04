package Main.AutomatedTests.Table3;

import Main.Algorithms.SupplyChainScheduling.BeamSearch.BeamSearch;
import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Algorithms.Heuristics.GeneticAlgorithm;
import Main.GlobalVars;
import Main.Graph.Graph;

import java.io.*;
import java.util.Scanner;

/**
 * for running the algorithm
 */
public class BSAutoTest {
    public static void main(String[] args) throws FileNotFoundException {

        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISFNodes-52-Customers.csv",
                "resources/ISFRoads.csv"
        );
//        Main.Graph originalGraph = Main.Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        // build the preprocessed graph
        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph reducedGraph = dijkstra.makeShortestPathGraph();
        reducedGraph.setIds();

        FileInputStream fileInputStream = new FileInputStream(new File("resources/Table3/t3-input-subset-03.csv"));
        Scanner sc = new Scanner(fileInputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/Table3/t3-automated-test-results-bs-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);
        out.println(sc.nextLine() + ",BBValue,BBTime,BBNodes");
        out.flush();

        while (sc.hasNextLine()) {
            String autoTestRow = sc.nextLine();
            System.out.println(autoTestRow);
            String testType = autoTestRow.split(",")[4];
            if (testType.equals("Exact")) continue;
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
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            int geneticTime = 0;
//            if (Main.GlobalVars.numberOfCustomers == 11) geneticTime = 1000;
//            if (Main.GlobalVars.numberOfCustomers == 12) geneticTime = 10000;

            // run the genetic algorithm
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                    preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 40);
            geneticAlgorithm.run(geneticTime);
//            geneticAlgorithm.printBestChromosome();


            String expandedNodes = "";
            String elapsedTime = "";
            String optimalValue = "";

            GlobalVars.startTime = System.currentTimeMillis();
            try {
                // run the branch and bound algorithm
                GlobalVars.startTime = System.currentTimeMillis();
                BeamSearch beamSearch = new BeamSearch(preprocessedGraph, Double.parseDouble(testType), GlobalVars.INF); // geneticAlgorithm.getMinimumCost()
                beamSearch.run(GlobalVars.depotName);
                GlobalVars.finishTime = System.currentTimeMillis();
                beamSearch.printTheAnswer();

                optimalValue = String.format("%.2f", beamSearch.minimumCost);
            } catch (NullPointerException e) {
                optimalValue = "NA";
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                optimalValue = "ML";
                e.printStackTrace();
            }
            GlobalVars.finishTime = System.currentTimeMillis();
            expandedNodes = "" + GlobalVars.numberOfBranchAndBoundNodes;
            elapsedTime = String.format("%.2f", (geneticTime + GlobalVars.finishTime - GlobalVars.startTime) / 1000.);


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