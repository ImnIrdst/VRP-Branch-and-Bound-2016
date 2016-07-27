package VRP.Algorithms.Heuristics;

/**
 * An Implementation of GA Algorithm used for
 * calculating an upper bound for our problem (VRPD)
 * Created by iman on 7/27/16.
 */
public class GeneticAlgorithm {
    private double minimumCost;

    private Chromosome[] population;

    /**
     * Constructor Creates a population with given qty
     */
    public GeneticAlgorithm(int customerQty, int vehiclesQty, int populationQty) {
        population = new Chromosome[populationQty];
        for (int i = 0; i < populationQty; i++) population[i] = new Chromosome(customerQty + vehiclesQty);

        initializePopulation(customerQty, vehiclesQty);
    }

    /**
     * run The algorithm to the given time
     *
     * @param computeDurationMileSecond is how much time can be consumed
     */
    public void run(int computeDurationMileSecond) {

    }

    /**
     * initializes the population by shuffling the ids of nodes
     */
    public void initializePopulation(int numberOfCustomers, int numberOfVehicles) {

    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome array of id of the nodes
     */
    public void mutate(int[] chromosome) {

    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome1 array of id of the nodes
     * @param chromosome2 array of id of the nodes
     * @return a new chromosome by crossover of two given chromosomes
     */
    public int[] crossOver(int[] chromosome1, int[] chromosome2) {
        return null;
    }

    /**
     * @param chromosome array of id of the nodes
     * @return the cost for given chromosome
     */
    public double fitnessFunction(int[] chromosome) {
        return 0;
    }

    /**
     * @return minimum cost so far
     */
    public double getTheMinimumCost() {
        return minimumCost;
    }

    private class Chromosome implements Comparable<Chromosome> {
        private int size;
        private int[] ids;

        public Chromosome(int size) {
            ids = new int[size];
        }

        public int getCost() {
            return 0;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.getCost(), o.getCost());
        }
    }
}
