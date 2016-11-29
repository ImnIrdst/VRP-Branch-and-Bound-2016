package Main.AutomatedTests.Experiment2;

import Main.Algorithms.SupplyChainScheduling.BranchAndBound.BranchAndBound;
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
public class BBGAAutoTest {
    private static final int INSTANCES_PER_TESTCASE = 10;

    public static void main(String[] args) throws FileNotFoundException {;
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.println("BEGIN BBGAAutoTest");
        GlobalVars.log.println(GlobalVars.plusesLine);

        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex2/ex2-automated-test-results-bb-ga-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        String tableHeader = "TestID," + SCSTestCase.getTableHeader() + ",Cost,CPUTime,Nodes,Upperbound";
        out.println(tableHeader);
        GlobalVars.log.println(tableHeader);

        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();

        for (int testId = 0; testGenerator.hasNextTestCase();) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                Graph originalGraph = Graph.buildRandomGraphFromTestCase(testCase, testId);
//                if (testId != 3586) continue;
//                if (testId > 5) continue;

                // fill the global variables
                originalGraph.setIds();
                GlobalVars.setTheGlobalVariables(originalGraph);
                GlobalVars.log.println(originalGraph.getVerticesFormattedString());
//            preprocessedGraph.printGraph();

                GlobalVars.log.println("Number of Customers, Vehicles: " +
                        GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);


                // run the genetic algorithm
                Main.Algorithms.Heuristics.GA.GA4.GeneticAlgorithm geneticAlgorithm =
                        new Main.Algorithms.Heuristics.GA.GA4.GeneticAlgorithm(
                                originalGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
                geneticAlgorithm.run(10000, 1000, 2000);
                GlobalVars.log.println(geneticAlgorithm.bestChromosomeString());


                String expandedNodes = "";
                String elapsedTime = "";
                String optimalValue = "";
                String upperBound = String.format("%.2f", geneticAlgorithm.getMinimumCost() + 1e-9);

                GlobalVars.startTime = System.currentTimeMillis();
                try {
                    // run the branch and bound algorithm
                    BranchAndBound branchAndBound = new BranchAndBound(originalGraph, geneticAlgorithm.getMinimumCost() + 1e-9); // geneticAlgorithm.getMinimumCost()
                    branchAndBound.run(GlobalVars.depotName);
                    branchAndBound.getTheAnswerFormattedString();
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
                elapsedTime = String.format("%.2f", (geneticAlgorithm.getElapsedTimeInSeconds() * 1000 + GlobalVars.finishTime - GlobalVars.startTime) / 1000.);
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

                System.out.println("BBGA: " + tableRow);
            }
        }
        out.close();
    }
}