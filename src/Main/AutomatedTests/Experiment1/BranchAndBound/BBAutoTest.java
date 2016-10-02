package Main.AutomatedTests.Experiment1.BranchAndBound;

import Main.Algorithms.Heuristics.GeneticAlgorithm;
import Main.Algorithms.SupplyChainScheduling.BranchAndBound.BranchAndBound;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.IOLoader.LoadRandomGraph;

import java.io.*;

/**
 * for running the algorithm
 */
public class BBAutoTest {
    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/ex1-automated-test-results-bb-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "Test ID,Customers,Vehicles,Cost,CPU Time,Nodes,";
        out.println(tableHeader);
        System.out.println(tableHeader);
        for (int testId=0 ; testId<100 ; testId++){
            Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(testId);

            // fill the global variables
            originalGraph.setIds();
            GlobalVars.setTheGlobalVariables(originalGraph);
//            preprocessedGraph.printVertices();
//            preprocessedGraph.printGraph();

            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            int geneticTime = 0;
//            if (Main.GlobalVars.numberOfCustomers == 11) geneticTime = 1000;
//            if (GlobalVars.numberOfCustomers == 12) geneticTime = 10000;

            // run the genetic algorithm
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                    originalGraph, GlobalVars.numberOfCustomers, 2, 40);
            geneticAlgorithm.run(geneticTime);
//            geneticAlgorithm.printBestChromosome();


            String expandedNodes = "";
            String elapsedTime = "";
            String optimalValue = "";

            GlobalVars.startTime = System.currentTimeMillis();
            try {
                // run the branch and bound algorithm
                BranchAndBound branchAndBound = new BranchAndBound(originalGraph, 1e6); // geneticAlgorithm.getMinimumCost()
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
            String tableRow = String.format("%d,%d,%d,%s,%s,%s", testId,
                    GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, optimalValue, elapsedTime, expandedNodes);

//            System.out.println(testInfo);
            System.out.println(tableHeader);
            System.out.println(tableRow);
            System.out.println("Optimal Value: " + optimalValue);
            System.out.println("Total Calculation time: " + elapsedTime + "s");
            System.out.println("Number of Branch and Bound Tree Nodes: " + expandedNodes);
            System.out.println("-------------------------------------");

            out.println(tableRow);
            out.flush();
        }
        out.close();
    }
}