package VRP.Algorithms.Heuristics;

import VRP.Graph.Graph;

import java.util.*;

/**
 * An Implementation of GA Algorithm used for
 * calculating an upper bound for our problem (VRPD)
 * Created by iman on 7/27/16.
 */
public class GeneticAlgorithm {

    private Graph graph;
    private int vehicleQty;
    private int customerQty;
    private int populationSize;
    private double minimumCost;
    private List<Chromosome> population;

    private long startTime;

    private final double mutationProbability = 0.001;

    /**
     * Constructor Creates a population with given qty
     */
    public GeneticAlgorithm(Graph graph, int customerQty, int vehicleQty, int populationSize) {
        this.graph = graph;
        this.vehicleQty = vehicleQty;
        this.customerQty = customerQty;
        this.populationSize = populationSize;
        this.minimumCost = Double.MAX_VALUE;
        this.population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++)
            population.add(new Chromosome(customerQty + vehicleQty));
    }

    /**
     * run The algorithm to the given time
     *
     * @param computeDurationMilliSecond is how much time can be consumed
     */
    public void run(int computeDurationMilliSecond) {
        startTime = System.currentTimeMillis();
        initializePopulation(customerQty, vehicleQty);

        while (System.currentTimeMillis() > startTime + computeDurationMilliSecond) {
            List<Chromosome> newPopulation = new ArrayList<>(population);

            // cross over
            for (Chromosome c1 : population) {
                for (Chromosome c2 : population) newPopulation.add(crossOver(c1, c2));
            }

            // mutate
            for (Chromosome c1 : population) mutate(c1);

            // selection
            population = selection(newPopulation);

            // update best answer
            minimumCost = population.get(0).getCost();
        }
    }

    /**
     * initializes the population by shuffling the ids of nodes
     */
    public void initializePopulation(int customerQty, int vehicleQty) {

        for (int i = 0; i < customerQty + vehicleQty; i++) {
            if (i < customerQty)
                population.get(i).set(i, i);
            else
                population.get(i).set(i, customerQty);

            population.get(i).shuffle(); // shuffle to generate a random population
        }
    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome array of id of the nodes
     */
    public void mutate(Chromosome chromosome) {

    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome1 array of id of the nodes
     * @param chromosome2 array of id of the nodes
     * @return a new chromosome by crossover of two given chromosomes
     */
    public Chromosome crossOver(Chromosome chromosome1, Chromosome chromosome2) {
        return null;
    }

    public List<Chromosome> selection(List<Chromosome> chromosomes) {
        return null;
    }

    /**
     * @param chromosome array of id of the nodes
     * @return the cost for given chromosome
     */
    public double fitnessFunction(Chromosome chromosome) {
        return 0;
    }

    /**
     * @return minimum cost so far
     */
    public double getTheMinimumCost() {
        return minimumCost;
    }

    /**
     * Chromosome class for a set of ids (for VRPD) problem
     */
    private class Chromosome implements Comparable<Chromosome> {
        public int size;
        public List<Integer> list;

        public Chromosome(int size) {
            list = new ArrayList<>(size);
        }

        public Chromosome(Chromosome chromosome) {
            this.size = chromosome.size;
            this.list = new ArrayList<>(chromosome.list);
        }

        public int get(int idx) {
            return list.get(idx);
        }

        public void set(int idx, int value) {
            list.set(idx, value);
        }

        public int getCost() {
            return 0;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.getCost(), o.getCost());
        }

        public void shuffle() {
            Collections.shuffle(list);
        }
    }
}
