package Main.AutomatedTests.Experiment1.GA3;

import Main.Algorithms.Heuristics.GA.GA4.GeneticAlgorithm;
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
public class Genetic3AutoTest {
    static final int testBatch = 5;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(
                new File("resources/Experiments/Ex1/ex1-automated-test-results-ga3-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);

        double sumOfCosts = 0;
        double sumOfTimes = 0;
        double sumOfIterations = 0;
        double sumOfChromosomes = 0;
        String tableHeader = "ID,TestID,Customers,Vehicles,Cost,Iterations,ChromosomesQty";
        out.println(tableHeader);
        System.out.println(tableHeader);
        for (int id = 0; id < 100 * testBatch; id++) {
            int testId = id / testBatch;

            Graph originalGraph = LoadRandomGraph.loadWithDoubleParams(testId);

            // fill the global variables
            originalGraph.setIds();
            GlobalVars.setTheGlobalVariables(originalGraph);
            originalGraph.printVertices();
//            preprocessedGraph.printGraph();

            System.out.println("Test # " + id);
            System.out.println("Number of Customers, Vehicles: " +
                    GlobalVars.numberOfCustomers + " " + GlobalVars.numberOfVehicles);

            int geneticTime = 10000;
            int maxIterationsNoUpdate = (int)(2000 / Math.log(GlobalVars.numberOfCustomers));
            maxIterationsNoUpdate = (maxIterationsNoUpdate/100) * 100; // round it

            // run the genetic algorithm
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                    originalGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 200);
            geneticAlgorithm.run(geneticTime, maxIterationsNoUpdate);
//            geneticAlgorithm.printBestChromosome();


            String iterations = "" + geneticAlgorithm.iterations;
            String iterationLimit = "" + maxIterationsNoUpdate;
            String time = String.format("%.2f", geneticAlgorithm.getElapsedTimeInSeconds());
            String cost = String.format("%.2f", geneticAlgorithm.getMinimumCost());

            String tableRow = String.format("%d,%d,%d,%d,%s,%s,%s,%s", id, testId,
                    GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, cost, time, iterations, iterationLimit);

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
            sumOfChromosomes += geneticAlgorithm.chromosomesQty;
            sumOfIterations += geneticAlgorithm.iterations;
            if ((id + 1) % testBatch == 0) {
                String averageRow = String.format("avg,%d,%d,%d,%.2f,%.2f,%.2f,%.2f", testId,
                        GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, sumOfCosts / testBatch,
                        sumOfTimes / testBatch, sumOfIterations / testBatch, sumOfChromosomes / testBatch);
                System.out.println(averageRow);

                out.println(averageRow);
                out.flush();

                sumOfCosts = 0;
                sumOfChromosomes = 0;
                sumOfIterations = 0;
            }
        }
        out.close();
    }
}