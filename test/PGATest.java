import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Algorithms.Heuristics.GA.GA2.GeneticAlgorithm;
import Main.Algorithms.Heuristics.PGA.PGAThread;
import Main.Graph.Graph;
import Main.GlobalVars;

import java.io.FileNotFoundException;

public class PGATest {
    private static final int THREADS_QTY = 10;
    private static final int TEST_ROUNDS = 100;
    private static final int COMPUTATION_TIME = 2000;
    private static final int POPULATION_SIZE = 40;

    public static void testGA(Graph preprocessedGraph) throws FileNotFoundException {
        double sum = 0;
        for (int i = 0; i < TEST_ROUNDS; i++) {
            // run the genetic algorithm
            GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                    preprocessedGraph, GlobalVars.numberOfCustomers, GlobalVars.numberOfVehicles, 40);
//            geneticAlgorithm.run(COMPUTATION_TIME);
            sum += geneticAlgorithm.getMinimumCost();

            if (i % 10 == 0) System.out.printf("GA iterations # %02d\n", i);
        }
        System.out.println("GA average: " + (sum / TEST_ROUNDS));
    }

    public static void testPGA(Graph preprocessedGraph) throws FileNotFoundException, InterruptedException {
        double overallSum = 0;
        for (int r = 0; r < TEST_ROUNDS; r++) {
            PGAThread[] pgaThreads = new PGAThread[THREADS_QTY];

            // build the preprocessed graph
            for (int i = 0; i < THREADS_QTY; i++) {
                pgaThreads[i] = new PGAThread(preprocessedGraph.getCopy(), POPULATION_SIZE, COMPUTATION_TIME, "Thread #" + i);
            }
            for (int i = 0; i < THREADS_QTY; i++) {
                pgaThreads[i].start();
            }

            for (int i = 0; i < THREADS_QTY; i++) {
                pgaThreads[i].join();
            }

            double minimum = GlobalVars.INF;
            for (int i = 0; i < THREADS_QTY; i++) {
                minimum = Math.min(minimum, pgaThreads[i].getResult());
            }
            overallSum += minimum;
            if (r % 10 == 0) System.out.printf("PGA iterations #% 02d\n", r);
        }
        System.out.println("PGA average: " + (overallSum / TEST_ROUNDS));
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Graph originalGraph = Graph.buildAGraphFromAttributeTables(
                "resources/ISF-12-Customers.csv",
                "resources/ISFRoads.csv"
        );
//        Main.Graph originalGraph = Main.Graph.buildAGraphFromCSVFile("resources/input.csv");
//        originalGraph.printGraph();

        Dijkstra dijkstra = new Dijkstra(originalGraph);
        Graph preprocessedGraph = dijkstra.makeShortestPathGraph();

        // fill the global variables
        GlobalVars.setTheGlobalVariables(preprocessedGraph);

        testGA(preprocessedGraph);
        testPGA(preprocessedGraph);
    }
}
