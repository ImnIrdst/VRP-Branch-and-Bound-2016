package Main.AutomatedTests.AutoTests;

import Main.Algorithms.Heuristics.DispatchingRules.ATC;
import Main.Algorithms.SupplyChainScheduling.BranchAndBound.BranchAndBound;
import Main.AutomatedTests.TestCases.SCSTestCase;
import Main.AutomatedTests.TestCases.SCSTestGenerator;
import Main.GlobalVars;
import Main.Graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * for running the algorithm
 */
public class BBATCAutoTest {
    public static int INSTANCES_PER_TESTCASE = 10;

    public static void main(String[] args) throws FileNotFoundException {
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.println("BEGIN BBATCAutoTest");
        GlobalVars.log.println(GlobalVars.plusesLine);

        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex2/ex2-automated-test-results-bb-atc-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "TestID," + SCSTestCase.getTableHeader() + ",Cost,CPUTime,Nodes,UpperBound";
        out.println(tableHeader);
        GlobalVars.log.println(tableHeader);

        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();

        for (int testId = 0; testGenerator.hasNextTestCase();) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                Graph originalGraph = Graph.buildRandomGraphFromTestCase(testCase, testId);
//                if (testId > 5) continue;

                // fill the global variables
                originalGraph.setIds();
                GlobalVars.setTheGlobalVariables(originalGraph);
                GlobalVars.log.println(originalGraph.getVerticesFormattedString());
//            preprocessedGraph.printGraph();

                GlobalVars.log.println("Number of Customers, Vehicles: " +
                        GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);


                // run the genetic algorithm
                ATC atc = new ATC(originalGraph);
                atc.run();
                GlobalVars.log.println("ATC: " + atc);

                String expandedNodes = "";
                String elapsedTime = "";
                String optimalValue = "";
                String upperBound = String.format("%.2f", atc.getMinimumCost() + 1e-9);

                GlobalVars.startTime = System.currentTimeMillis();
                try {
                    // run the branch and bound algorithm
                    BranchAndBound branchAndBound = new BranchAndBound(originalGraph, atc.getMinimumCost() + 1e-9); // geneticAlgorithm.getMinimumCost()
                    branchAndBound.run(GlobalVars.depotName);
                    GlobalVars.log.println(branchAndBound.getTheAnswerFormattedString());
                    GlobalVars.log.println(GlobalVars.dashesLine);
                    GlobalVars.finishTime = System.currentTimeMillis();
                    GlobalVars.log.printf("Optimal Cost: %.2f\n", branchAndBound.bestNode.getCost());

                    optimalValue = String.format("%.2f", branchAndBound.minimumCost);
                } catch (NullPointerException e) {
                    optimalValue = "NA";
                } catch (OutOfMemoryError e) {
                    optimalValue = "ML";
                }
                GlobalVars.finishTime = System.currentTimeMillis();
                expandedNodes = "" + GlobalVars.numberOfBranchAndBoundNodes;
                elapsedTime = String.format("%.2f", (atc.getElapsedTimeInSeconds() * 1000 + GlobalVars.finishTime - GlobalVars.startTime) / 1000.);
                String tableRow = String.format("%d,%s,%s,%s,%s,%s", testId, testCase.getCSVRow(),
                        optimalValue, elapsedTime, expandedNodes, upperBound);

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

                System.out.println("BBATC: " + tableRow);
            }
        }
        out.close();
    }
}