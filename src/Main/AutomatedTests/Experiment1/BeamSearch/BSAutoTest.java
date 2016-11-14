package Main.AutomatedTests.Experiment1.BeamSearch;

import Main.Algorithms.SupplyChainScheduling.BeamSearch.BeamSearch;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.IOLoader.LoadRandomGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * for running the algorithm
 */
public class BSAutoTest {
    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex1/ex1-automated-test-results-bs-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "Test ID,Customers,Vehicles,Cost,CPU Time,Nodes,UpperBound";
        out.println(tableHeader);
        System.out.println(tableHeader);
        for (int testId = 0; testId < 100; testId++) {
//            if (testId == 0) continue;
            Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(testId);

            // fill the global variables
            originalGraph.setIds();
            GlobalVars.setTheGlobalVariables(originalGraph);
            originalGraph.getVerticesFormattedString();
//            preprocessedGraph.printGraph();

            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            String expandedNodes = "";
            String elapsedTime = "";
            String optimalValue = "";
            double theta0 = Math.min(1.0, 1.1 - (GlobalVars.numberOfCustomers / 100.0));

            GlobalVars.startTime = System.currentTimeMillis();
            try {
                // run the branch and bound algorithm

                BeamSearch beamSearch = new BeamSearch(originalGraph, theta0, GlobalVars.INF); // geneticAlgorithm.getMinimumCost()
                beamSearch.run(GlobalVars.depotName);
                GlobalVars.finishTime = System.currentTimeMillis();
                System.out.printf("Optimal Cost: %.2f\n", beamSearch.bestNode.getCost());

                optimalValue = String.format("%.2f", beamSearch.minimumCost);
            } catch (NullPointerException e) {
                optimalValue = "NA";
            } catch (OutOfMemoryError e) {
                optimalValue = "ML";
            }
            GlobalVars.finishTime = System.currentTimeMillis();
            expandedNodes = "" + GlobalVars.numberOfBranchAndBoundNodes;
            elapsedTime = String.format("%.2f", (GlobalVars.finishTime - GlobalVars.startTime) / 1000.);
            String tableRow = String.format("%d,%d,%d,%s,%s,%s,%.2f", testId,
                    GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles,
                    optimalValue, elapsedTime, expandedNodes, theta0);

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