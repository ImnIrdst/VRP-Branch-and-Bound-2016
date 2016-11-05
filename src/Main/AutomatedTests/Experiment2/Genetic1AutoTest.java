package Main.AutomatedTests.Experiment2;

import Main.Algorithms.Heuristics.GA.GA1.GeneticAlgorithm;
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
public class Genetic1AutoTest {
    static final int testBatch = 10;
    private static final int INSTANCES_PER_TESTCASE = 10;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex2/ex2-automated-test-results-ga1-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        int id = 0;
        double sumOfChromosomeQty = 0;
        double sumOfCosts = 0;
        double sumOfTimes = 0;
        double sumOfIterations = 0;

        String tableHeader = "ID,TestID," + SCSTestCase.getTableHeader() + ",Cost,Time,Iterations,ChromosomeQty";
        out.println(tableHeader);
        System.out.println(tableHeader);

        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();
        testGenerator.addBigTestsV1();
        for (int testId = 0; testGenerator.hasNextTestCase(); testId++) {
            SCSTestCase testCase = testGenerator.getNextTestCase();
            for (int i = 0; i < INSTANCES_PER_TESTCASE; i++, testId++) {
                for (int batch = 0; batch < testBatch; batch++, id++) {
                    Graph originalGraph = Graph.buildRandomGraphFromTestCase(testCase, testId);

                    // fill the global variables
                    originalGraph.setIds();
                    GlobalVars.setTheGlobalVariables(originalGraph);
                    originalGraph.printVertices();
//                preprocessedGraph.printGraph();

                    System.out.println("Test # " + id);
                    System.out.println("Number of Customers, Vehicles: " +
                            GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

                    int geneticTime = 100000;
                    int maxIterationsNoUpdate = 1000;

                    // run the genetic algorithm
                    GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                            originalGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
                    geneticAlgorithm.run(geneticTime, maxIterationsNoUpdate);
//            geneticAlgorithm.printBestChromosome();


                    String iterations = "" + geneticAlgorithm.iterations;
                    String iterationLimit = "" + maxIterationsNoUpdate;
                    String time = String.format("%.2f", geneticAlgorithm.getElapsedTimeInSeconds());
                    String cost = String.format("%.2f", geneticAlgorithm.getMinimumCost());

                    String tableRow = String.format("%d,%d,%s,%s,%s,%s,%s", id, testId,
                            testCase.getCSVRow(), cost, time, iterations, geneticAlgorithm.chromosomesQty);

//            System.out.println(testInfo);
                    System.out.println(tableHeader);
                    System.out.println(tableRow);
                    System.out.println("Optimal Value: " + cost);
                    System.out.println("Total Calculation time: " + iterations + "s");
                    System.out.println("-------------------------------------");

                    out.println(tableRow);
                    out.flush();

                    sumOfCosts += geneticAlgorithm.getMinimumCost();
                    sumOfTimes += geneticAlgorithm.getElapsedTimeInSeconds();
                    sumOfIterations += geneticAlgorithm.iterations;
                    sumOfChromosomeQty += geneticAlgorithm.chromosomesQty;
                    if ((id + 1) % testBatch == 0) {
                        String averageRow = String.format("avg,%d,%s,%.2f,%.2f,%.0f,%.0f",
                                testId, testCase.getCSVRow(), sumOfCosts / testBatch,
                                sumOfTimes / testBatch, sumOfIterations / testBatch, sumOfChromosomeQty / testBatch);
                        System.out.println(averageRow);

                        out.println(averageRow);
                        out.flush();

                        sumOfCosts = 0;
                        sumOfTimes = 0;
                        sumOfIterations = 0;
                        sumOfChromosomeQty = 0;
                    }
                }
            }
        }
        out.close();
    }
}