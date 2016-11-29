package Main.AutomatedTests.Experiment2;

import Main.Algorithms.Heuristics.DispatchingRules.ATC;
import Main.Algorithms.Other.Random;
import Main.Algorithms.SupplyChainScheduling.BeamSearch.BeamSearch;
import Main.AutomatedTests.SCSTests.SCSTestCase;
import Main.AutomatedTests.SCSTests.SCSTestGenerator;
import Main.GlobalVars;
import Main.Graph.Graph;

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
//        GlobalVars.log = new PrintWriter(System.out);
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.println("BEGIN BSAutoTest");
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.flush();

        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex2/ex2-automated-test-results-bs-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "ID,TestID," + SCSTestCase.getTableHeader() + ",Cost,CPUTime,Nodes,theta0";
        out.println(tableHeader);
        GlobalVars.log.println(tableHeader);

        double theta0 = 1;
        int id = 0, testBatch = 10;
        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();
        testGenerator.addBigTestsV1();
        for (int testId = 0; testGenerator.hasNextTestCase(); ) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                double sumOfCosts = 0;
                double sumOfTimes = 0;
                double sumOfNodes = 0;
                for (int batch = 0; batch < testBatch; batch++, id++) {
//                    if (id % 10000 == 0) System.out.println(id);
//                    if (id != 100000) continue;

                    Graph originalGraph = Graph.buildRandomGraphFromTestCase(testCase, testId);
                    Random.setSeed(System.currentTimeMillis());
//                    if (id % 1117 == 0) System.out.println(id);

//                    if (testId > 5) continue;

                    // fill the global variables
                    originalGraph.setIds();
                    GlobalVars.setTheGlobalVariables(originalGraph);
                    GlobalVars.log.println(originalGraph.getVerticesFormattedString());
//            preprocessedGraph.printGraph();

                    GlobalVars.log.println("Number of Customers, Vehicles: " +
                            GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);


                    String expandedNodes = "";
                    String elapsedTime = "";
                    String optimalValue = "";

                    GlobalVars.startTime = System.currentTimeMillis();
                    theta0 = 1 / (Math.log10(GlobalVars.numberOfCustomers + 6));
                    ATC atc = new ATC(originalGraph);
                    atc.run();
                    GlobalVars.log.println(atc);

                    try {

                        // run the branch and bound algorithm
                        BeamSearch beamSearch = new BeamSearch(originalGraph, theta0, atc.getMinimumCost() + 1e-9); // geneticAlgorithm.getMinimumCost()
                        beamSearch.run(GlobalVars.depotName);
                        beamSearch.printTheAnswer();
                        GlobalVars.log.println(GlobalVars.dashesLine);
                        GlobalVars.finishTime = System.currentTimeMillis();
                        GlobalVars.log.printf("Optimal Cost: %.2f\n", beamSearch.bestNode.getCost());

                        optimalValue = String.format("%.2f", beamSearch.minimumCost);

                        sumOfCosts += beamSearch.minimumCost;
                    } catch (NullPointerException e) {
                        optimalValue = String.format("%.2f", atc.getMinimumCost());
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

//            GlobalVars.log.println(testInfo);
                    GlobalVars.log.println(tableHeader);
                    GlobalVars.log.println(tableRow);
                    GlobalVars.log.println("Optimal Value: " + optimalValue);
                    GlobalVars.log.println("Total Calculation time: " + elapsedTime + "s");
                    GlobalVars.log.println("Number of Branch and Bound Tree Nodes: " + expandedNodes);
                    GlobalVars.log.println("-------------------------------------");

                    out.println(tableRow);
                    out.flush();
                    GlobalVars.log.flush();

                    System.out.println("BS: " + tableRow);
                }
                String tableRow = String.format("avg,%d,%s,%.2f,%.2f,%.0f,%.2f", testId, testCase.getCSVRow(),
                        sumOfCosts / testBatch, sumOfTimes / testBatch, sumOfNodes / testBatch, theta0);
                out.println(tableRow);
                out.flush();

            }
        }
        out.close();
    }
}