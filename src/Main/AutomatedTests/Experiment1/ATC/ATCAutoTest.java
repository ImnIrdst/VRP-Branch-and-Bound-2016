package Main.AutomatedTests.Experiment1.ATC;

import Main.Algorithms.Heuristics.DispatchingRules.ATC;
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
public class ATCAutoTest {
    static final int testBatch = 5;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex1/ex1-automated-test-results-atc-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        double sumOfCosts = 0;
        double sumOfIterations = 0;
        double sumOfChromosomes = 0;
        String tableHeader = "ID,TestID,Customers,Vehicles,Cost,Iterations,ChromosomesQty";
        out.println(tableHeader);
        System.out.println(tableHeader);
        for (int id = 0; id < 100; id++) {
            int testId = id; // id / testBatch;

            Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(testId);

            // fill the global variables
            originalGraph.setIds();
            GlobalVars.setTheGlobalVariables(originalGraph);
            originalGraph.getVerticesFormattedString();
//            preprocessedGraph.printGraph();

            System.out.println("Test # " + id);
            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            // run atc algorithm
            ATC atc = new ATC(originalGraph);
            atc.run();

            String time = String.format("%.2f", atc.getElapsedTimeInSeconds());
            String cost = String.format("%.2f", atc.getMinimumCost());
            if (atc.getMinimumCost() > GlobalVars.INF - 1e-9) cost = "NA";


            String tableRow = String.format("%d,%d,%d,%d,%s,%s", id, testId,
                    GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, cost, time);

//            System.out.println(testInfo);
            System.out.println(tableHeader);
            System.out.println(tableRow);
            System.out.println("Optimal Value: " + cost);
            System.out.println("Total Calculation time: " + time + "s");
            System.out.println("-------------------------------------");

            out.println(tableRow);
            out.flush();
        }
        out.close();
    }
}