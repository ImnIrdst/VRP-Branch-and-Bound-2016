package Main.AutomatedTests.Experiment2;

import Main.Algorithms.Heuristics.DispatchingRules.ATC;
import Main.Algorithms.Heuristics.GA.GA1.GeneticAlgorithm;
import Main.Algorithms.Other.Random;
import Main.Algorithms.SupplyChainScheduling.BeamSearch.BeamSearch;
import Main.Algorithms.SupplyChainScheduling.BranchAndBound.BranchAndBound;
import Main.AutomatedTests.SCSTests.SCSTestCase;
import Main.AutomatedTests.SCSTests.SCSTestGenerator;
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
    private static final int INSTANCES_PER_TESTCASE = 10;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex2/ex2-automated-test-results-bs-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "ID,TestID," + SCSTestCase.getTableHeader() + ",Cost,CPUTime,Nodes,theta0";
        out.println(tableHeader);
        System.out.println(tableHeader);

        int id = 0, testBatch = 10;
        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();
        testGenerator.addBigTestsV1();
        for (int testId = 0; testGenerator.hasNextTestCase(); testId++) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                double sumOfCosts = 0;
                double sumOfTimes = 0;
                double sumOfNodes = 0;
                for (int batch = 0; batch < testBatch; batch++, id++) {
                    Graph originalGraph = Graph.buildRandomGraphFromTestCase(testCase, testId);
                    Random.setSeed(System.currentTimeMillis());
//            if (testId != 485) continue;

                    // fill the global variables
                    originalGraph.setIds();
                    GlobalVars.setTheGlobalVariables(originalGraph);
                    originalGraph.printVertices();
//            preprocessedGraph.printGraph();

                    System.out.println("Number of Customers, Vehicles: " +
                            GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);


                    String expandedNodes = "";
                    String elapsedTime = "";
                    String optimalValue = "";

                    GlobalVars.startTime = System.currentTimeMillis();
                    double theta0 = 1 / (Math.log10(GlobalVars.numberOfCustomers + 6));
                    try {
                        // run the branch and bound algorithm
                        BeamSearch beamSearch = new BeamSearch(originalGraph, theta0, GlobalVars.INF); // geneticAlgorithm.getMinimumCost()
                        beamSearch.run(GlobalVars.depotName);
                        beamSearch.printTheAnswer();
                        System.out.println(GlobalVars.dashesLine);
                        GlobalVars.finishTime = System.currentTimeMillis();
                        System.out.printf("Optimal Cost: %.2f\n", beamSearch.bestNode.getCost());

                        optimalValue = String.format("%.2f", beamSearch.minimumCost);

                        sumOfCosts += beamSearch.minimumCost;
                    } catch (NullPointerException e) {
                        optimalValue = "NA";
                    } catch (OutOfMemoryError e) {
                        optimalValue = "ML";
                    }
                    GlobalVars.finishTime = System.currentTimeMillis();
                    sumOfNodes += (GlobalVars.numberOfBranchAndBoundNodes);
                    sumOfTimes += (GlobalVars.finishTime - GlobalVars.startTime) / 1000.;

                    expandedNodes = "" + GlobalVars.numberOfBranchAndBoundNodes;
                    elapsedTime = String.format("%.2f", (GlobalVars.finishTime - GlobalVars.startTime) / 1000.);
                    String tableRow = String.format("%d,%d,%s,%s,%s,%s,%.2f", id, testId, testCase.getCSVRow(),
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
                String tableRow = String.format("avg,%d,%s,%.2f,%.2f,%.0f,%s", testId, testCase.getCSVRow(),
                        sumOfCosts / testBatch, sumOfTimes / testBatch, sumOfNodes / testBatch, "^");
                out.println(tableRow);
                out.flush();
            }
        }
        out.close();
    }
}