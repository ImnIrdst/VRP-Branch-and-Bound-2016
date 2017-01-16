package Main.AutomatedTests.AutoTests;

import Main.Algorithms.Heuristics.GA.GA4.GeneticAlgorithm;
import Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestCase;
import Main.AutomatedTests.TestCases.IntegerTestCase.SCSTestGenerator;
import Main.GlobalVars;
import Main.Graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * for running the algorithm
 */
public class Genetic4AutoTest {
    static final int testBatch = 10;
    private static final int INSTANCES_PER_TESTCASE = 10;

    public static void main(String[] args) throws FileNotFoundException {
        GlobalVars.log.println(GlobalVars.plusesLine);
        GlobalVars.log.println("BEGIN Genetic3AutoTest");
        GlobalVars.log.println(GlobalVars.plusesLine);

        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/AutoTestResults/ga4-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);


        String tableHeader = "ID,TestID," + SCSTestCase.getTableHeader() + ",Cost,Time,Iterations,ChromosomeQty";
        out.println(tableHeader);
        GlobalVars.log.println(tableHeader);

        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();
        testGenerator.addBigTestsV1();

        int id = 0;
        for (int testId = 0; testGenerator.hasNextTestCase();) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                for (int batch = 0; batch < testBatch; batch++, id++) {
                    if (id > 100) break;

                    Graph originalGraph = Graph.buildRandomGraphFromIntegerTestCase(testCase, testId);

                    // fill the global variables
                    originalGraph.setIds();
                    GlobalVars.setTheGlobalVariables(originalGraph);
                    GlobalVars.log.println(originalGraph.getVerticesFormattedString());
                    GlobalVars.log.println(originalGraph.getAdjacencyMatrixFormattedString());

                    int geneticTime = 100000;

                    // run the genetic algorithm
                    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                            originalGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
                    geneticAlgorithm.run(geneticTime, 1000, 2000);
                    GlobalVars.log.println(geneticAlgorithm.bestChromosomeString());
                    GlobalVars.log.println(geneticAlgorithm.bestChromosomeCostDetailsString());
                    GlobalVars.log.println(GlobalVars.equalsLine);

                    String iterations = "" + geneticAlgorithm.iterations;
                    String iterationLimit = "" + 1000 + " | " + 2000;
                    String time = String.format("%.2f", geneticAlgorithm.getElapsedTimeInSeconds());
                    String cost = String.format("%.2f", geneticAlgorithm.getMinimumCost());

                    String tableRow = String.format("%d,%d,%s,%s,%s,%s,%s", id, testId,
                            testCase.getCSVRow(), cost, time, iterations, geneticAlgorithm.chromosomesQty);

                    GlobalVars.log.println("Optimal Value: " + cost);
                    GlobalVars.log.println("Total Calculation time: " + iterations + "s");
                    GlobalVars.log.println("-------------------------------------");
                    // GlobalVars.log.println(tableHeader);
                    GlobalVars.log.println(tableRow);
                    GlobalVars.log.println("-------------------------------------");

                    out.println(tableRow);
                    out.flush();
                    GlobalVars.log.flush();

                    System.out.println("GA4: " + tableRow);
                }
            }
        }
        out.close();
    }
}