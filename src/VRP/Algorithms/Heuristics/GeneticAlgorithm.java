package VRP.Algorithms.Heuristics;

import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

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
    private Chromosome bestChromosome;
    private List<Chromosome> population;

    private final double MUTATION_PROBABILITY = 0.5;

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
    }

    /**
     * run The algorithm to the given time
     *
     * @param computeDurationMilliSecond is how much time can be consumed
     */
    public void run(int computeDurationMilliSecond) {
        System.out.println("--------------------------");
        System.out.println("Genetic algorithm");
        System.out.println("--------------------------");

        long startTime = System.currentTimeMillis();
        long printTime = startTime + GlobalVars.printTimeStepSize;
        initializePopulation(customerQty, vehicleQty);

        int iteration = 0;
        while (System.currentTimeMillis() < startTime + computeDurationMilliSecond) {
            List<Chromosome> newPopulation = new ArrayList<>();

            // cross over
            for (Chromosome c1 : population) {
                for (Chromosome c2 : population)
                    newPopulation.add(crossOver(c1, c2));
            }

            // mutate
            for (Chromosome c1 : population) mutate(c1);

            // selection
            population = selection(newPopulation);

            // update best answer
            if (population.get(0).getCost() < minimumCost){
                minimumCost = population.get(0).getCost();
                bestChromosome = population.get(0);
            }

            // print the progress
            if (System.currentTimeMillis() > printTime) {
                printTime += GlobalVars.printTimeStepSize;
                System.out.printf("Iteration #%d,\t\tTime elapsed: %.2fs,\t\tMinimum Cost: %.2f\n",
                        iteration, (System.currentTimeMillis() - startTime) / 1000., minimumCost);
            }
            iteration++;
        }
    }

    /**
     * initializes the population by shuffling the ids of nodes
     */
    public void initializePopulation(int customerQty, int vehicleQty) {
        int size = customerQty + vehicleQty - 1;
        for (int i = 0; i < populationSize; i++) {
            population.add(getRandomChromosome(size));
        }
    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome array of id of the nodes
     */
    public void mutate(Chromosome chromosome) {
        if (getRandInt(1000) / 1000. > MUTATION_PROBABILITY)
            return;

        int i = getRandInt(chromosome.size);
        int j = getRandInt(chromosome.size);

        // swap
        int tmp = chromosome.get(i);
        chromosome.set(i, chromosome.get(j));
        chromosome.set(j, tmp);
    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome1 array of id of the nodes
     * @param chromosome2 array of id of the nodes
     * @return a new chromosome by crossover of two given chromosomes
     */
    public Chromosome crossOver(Chromosome chromosome1, Chromosome chromosome2) {
        int size = chromosome1.size;
        int mid = getRandInt(size);
        Chromosome newChromosome = new Chromosome();

        int[] usedNodes = new int[customerQty + 1];
        for (int i = 0; i < customerQty; i++) usedNodes[i] = 1;
        usedNodes[customerQty] = GlobalVars.numberOfVehicles - 1;

        for (int i = 0; i < mid; i++) {
            newChromosome.add(chromosome1.get(i));
            usedNodes[chromosome1.get(i)]--;
        }

        for (int i = 0; i < size; i++) {
            if (usedNodes[chromosome2.get(i)] > 0) {
                usedNodes[chromosome2.get(i)]--;
                newChromosome.add(chromosome2.get(i));
            }

        }


        return newChromosome;
    }

    /**
     * Selects top chromosomes from old population and their children
     */
    public List<Chromosome> selection(List<Chromosome> chromosomes) {
        int size = customerQty + vehicleQty - 1;
        List<Chromosome> newPopulation = new ArrayList<>();

        // new population
        for (int i = 0; i < populationSize / 4; i++) {
            newPopulation.add(getRandomChromosome(size));
        }
        // select randomly
        for (int i = 0; i < populationSize / 4; i++) {
            newPopulation.add(chromosomes.get(i));
        }

        // select top nodes
        Collections.sort(chromosomes);
        for (int i = 0; i < populationSize / 2; i++) {
            newPopulation.add(chromosomes.get(i));
        }

        Collections.sort(newPopulation);
        return newPopulation;
    }

    /**
     * @return minimum cost so far
     */
    public double getMinimumCost() {
        return minimumCost;
    }

    public void printBestChromosome(){
        System.out.println("Best Chromosome: " + bestChromosome + ", " + minimumCost);
    }

    public Chromosome getRandomChromosome(int size) {
        Chromosome newChromosome = new Chromosome();

        for (int j = 0; j < size; j++) {
            if (j < customerQty)
                newChromosome.add(j);
            else
                newChromosome.add(GlobalVars.depotId);
        }
        Collections.shuffle(newChromosome.list); // shuffle to generate a random population

        return newChromosome;
    }

    /**
     * @return a random number less than given bound
     */
    public int getRandInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    /**
     * Chromosome class for a set of ids (for VRPD) problem
     */
    private class Chromosome implements Comparable<Chromosome> {
        public int size;
        public List<Integer> list;

        private int cost;
        private boolean isCostCalculated = false;

        /**
         * default constructor
         */
        public Chromosome() {
            list = new ArrayList<>();
        }

        /**
         * copy constructor
         */
        public Chromosome(Chromosome chromosome) {
            this.size = chromosome.size;
            this.list = new ArrayList<>(chromosome.list);
        }

        /**
         * get value in idx position
         */
        public int get(int idx) {
            return list.get(idx);
        }

        /**
         * set value in idx position
         */
        public void set(int idx, int value) {
            list.set(idx, value);
        }

        /**
         * adds the value to the end of list
         */
        public void add(int value) {
            list.add(value);
            size++;
        }

        /**
         * fitness function for this chromosome
         */
        public double getCost() {
            if (isCostCalculated == true)
                return cost;

            int remainedCapacity = 0;
            int vehiclesUsageCost = 0;
            int servicedCustomersQty = 0;

            double cumulativeTimeTaken = 0;
            double timeElapsedOnThisPath = 0;
            double cumulativePenaltyTaken = 0;

            List<Integer> tmpList = new ArrayList<>(list);
            tmpList.add(GlobalVars.depotId);

            Vertex u = graph.getVertexById(GlobalVars.depotId);
            for (int i = 0; i < tmpList.size() - 1; i++) {
                Vertex v = graph.getVertexById(tmpList.get(i));
                if (u.getId() == v.getId()) continue;

                if (u.type != VertexType.DEPOT) {
                    cumulativeTimeTaken += graph.getDistance(u, v);
                    timeElapsedOnThisPath += graph.getDistance(u, v);
                }

                if (u.type == VertexType.DEPOT && v.hasVehicle == 0)
                    return GlobalVars.INF;

                if (u.type == VertexType.DEPOT && v.hasVehicle == 1) {
                    remainedCapacity = v.capacity;
                    vehiclesUsageCost += v.fixedCost;
                    timeElapsedOnThisPath = v.mdt;
                }

                if (v.type == VertexType.DEPOT && timeElapsedOnThisPath > v.dueDate) {
                    cumulativePenaltyTaken += (timeElapsedOnThisPath - v.dueDate) * v.penalty;
                }

                if (v.type == VertexType.CUSTOMER) {
                    servicedCustomersQty++;
                    remainedCapacity -= v.demand;
                }

                if (v.type == VertexType.DEPOT) {
                    if (servicedCustomersQty == GlobalVars.numberOfCustomers)
                        return vehiclesUsageCost + cumulativePenaltyTaken + cumulativeTimeTaken;
                }

                if (remainedCapacity < 0) return GlobalVars.INF;

                u = v;
            }
            return GlobalVars.INF;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.getCost(), o.getCost());
        }

        @Override
        public String toString() {
            return list.toString();
        }
    }
}
