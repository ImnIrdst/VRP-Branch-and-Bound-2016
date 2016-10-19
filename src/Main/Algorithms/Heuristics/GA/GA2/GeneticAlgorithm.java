package Main.Algorithms.Heuristics.GA.GA2;

import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

import java.util.*;

/**
 * An Implementation of GA1 Algorithm used for
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

    private final double MUTATION_PROBABILITY = 0.05;
    private final double CROSSOVER_PROBABILITY = 1.00;

    private final boolean IS_VERBOSE = true;
    private final boolean IS_DEBUG_MODE = false;

    private int depotId;
    private long printTimeStepSize;

    public long chromosomesQty = 0;
    public long iterations = 0;

    private long startTime = 0;
    private long finishTime = 0;


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

        this.depotId = GlobalVars.depotId;
        this.printTimeStepSize = GlobalVars.printTimeStepSize;
    }

    /**
     * run The algorithm to the given time
     *
     * @param computeDurationMilliSecond is how much time can be consumed
     */
    public void run(int computeDurationMilliSecond, int maxIterationsNoUpdate) {
        if (IS_VERBOSE) {
            System.out.println(GlobalVars.equalsLine);
            System.out.println("\t\t\t\t\t\t\t\t\tGenetic algorithm");
            System.out.println(GlobalVars.equalsLine);
        }

        startTime = System.currentTimeMillis();
        long printTime = startTime + printTimeStepSize;
        long iterationsNoUpdate = 0;

        initializePopulation(customerQty, vehicleQty);
        while (System.currentTimeMillis() < startTime + computeDurationMilliSecond) {
            List<Chromosome> newPopulation = new ArrayList<>();

            // cross over
            for (Chromosome c1 : population) {
                for (Chromosome c2 : population) {
                    if (getRandom0to1() < CROSSOVER_PROBABILITY) {
                        newPopulation.add(crossOver(c1, c2));
                        chromosomesQty++;
                    }
                }
            }

            // mutate
            for (Chromosome c1 : population) mutate(c1);

            // selection
            population = selection(newPopulation);

            // update best answer
            if (population.get(0).getCost() < minimumCost) {
                minimumCost = population.get(0).getCost();
                bestChromosome = population.get(0);
                iterationsNoUpdate = 0;
            }

            // print the progress
            if (IS_VERBOSE && System.currentTimeMillis() > printTime) {
                printTime += printTimeStepSize;
                System.out.printf("Iteration #%d,\tTime elapsed: %.2fs,\tChromosomesQty: %d,\tMinimum Cost: %.2f\n",
                        iterations, (System.currentTimeMillis() - startTime) / 1000., chromosomesQty, minimumCost);
            }

            if (iterationsNoUpdate > maxIterationsNoUpdate)
                break;


            iterations++;
            iterationsNoUpdate++;
        }

        finishTime = System.currentTimeMillis();
    }

    /**
     * gets a random population with shuffling
     */
    public Chromosome getRandomChromosome1(int size) {
        Chromosome newChromosome = new Chromosome();

        for (int j = 0; j < customerQty; j++) {
            newChromosome.add(getRandInt(vehicleQty + 1));
        }

        return newChromosome;
    }

    public Chromosome getRandomChromosome(int size) {
        List<Integer> customers = new ArrayList<>();

        for (int j = 0; j < vehicleQty + 1; j++) {
            customers.add(j);
        }

        Chromosome newChromosome = new Chromosome();

        int[] remainedCapacity = new int[vehicleQty + 1];
        Arrays.fill(remainedCapacity, GlobalVars.depot.capacity);

        for (int i = 0; i < size && customers.size() > 0; i++) {
            Collections.shuffle(customers);

            int vId = -1;
            while (true) {
                vId = customers.get(customers.size() - 1);
                if (remainedCapacity[vId] > 0) {
                    remainedCapacity[vId]--;
                    break;
                }
                customers.remove(customers.size() - 1);
            }

            newChromosome.add(vId);
        }

        return newChromosome;
    }

    /**
     * initializes the population by shuffling the ids of nodes
     */
    public void initializePopulation(int customerQty, int vehicleQty) {
        int size = customerQty;
        for (int i = 0; i < populationSize; i++) {
            population.add(getRandomChromosome(size));
            chromosomesQty++;
        }
    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome array of id of the nodes
     */
    public void mutate1(Chromosome chromosome) {
        if (getRandom0to1() > MUTATION_PROBABILITY)
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
     * @param chromosome array of id of the nodes
     */
    public void mutate(Chromosome chromosome) {
        if (getRandom0to1() > MUTATION_PROBABILITY)
            return;

        int i = getRandInt(chromosome.size);
        int j = getRandInt(chromosome.size);

        if (i > j) {
            int t = i;
            i = j;
            j = t;
        }

        List<Integer> temp = new ArrayList<>();
        for (int k = i; k <= j; k++) {
            temp.add(chromosome.get(k));
        }
        Collections.shuffle(temp);

        for (int k = i; k <= j; k++) {
            chromosome.set(k, temp.get(k - i));
        }
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

        int[] remainedCapacity = new int[vehicleQty + 1];
        Arrays.fill(remainedCapacity, GlobalVars.depot.capacity);

        for (int i = 0; i < mid; i++) {
            if (remainedCapacity[chromosome1.get(i)] <= 0) continue;

            newChromosome.add(chromosome1.get(i));
            remainedCapacity[chromosome1.get(i)]--;
        }

        for (int i = 0; i < size && newChromosome.size < size; i++) {
            if (remainedCapacity[chromosome2.get(i)] <= 0) continue;

            newChromosome.add(chromosome2.get(i));
            remainedCapacity[chromosome2.get(i)]--;
        }

        return newChromosome;
    }

    /**
     * Selects top chromosomes from old population and their children
     */
    public List<Chromosome> selection(List<Chromosome> chromosomes) {
        List<Chromosome> newPopulation = new ArrayList<>();

        // select top nodes
        Collections.sort(chromosomes);

        for (int i = 0; i < 9 * (populationSize / 10); i++) {
            newPopulation.add(chromosomes.get(i));
        }

        for (int i = chromosomes.size() - populationSize / 10; i < chromosomes.size(); i++) {
            newPopulation.add(chromosomes.get(i));
        }

        return newPopulation;
    }

    /**
     * @return minimum cost so far
     */
    public double getMinimumCost() {
        return minimumCost;
    }

    public double getElapsedTimeInSeconds() {
        return (finishTime - startTime) / 1000.0;
    }

    public void printBestChromosome() {
        System.out.println("Best Chromosome: " + bestChromosome + ", " + String.format("%.8f", minimumCost));
    }


    /**
     * @return a random number less than given bound
     */
    public int getRandInt(int bound) {
        Random random = new Random();
        return random.nextInt(bound);
    }

    /**
     * @return a random number between 0, 1 for probability
     */
    public double getRandom0to1() {
        return getRandInt(1001) / 1000.;
    }

    /**
     * Chromosome class for a set of ids (for VRPD) problem
     */
    private class Chromosome implements Comparable<Chromosome> {
        public int size;
        public List<Integer> list;

        private double cost;
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

        List<Integer> orderThem1(List<Integer> customers) {
            Collections.sort(customers, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return Double.compare(graph.getVertexById(o1).dueDate, graph.getVertexById(o2).dueDate);
                }
            });

            return null;
        }

        List<Integer> orderThem(List<Integer> customers){
            Collections.shuffle(customers);

            return null;
        }

        /**
         * fitness function for this chromosome
         */
        public double getCost() {
            if (isCostCalculated == true)
                return cost;

            List<Integer>[] batch = new ArrayList[vehicleQty + 1];
            for (int i=0 ; i<list.size() ; i++){
                if (batch[list.get(i)] == null)
                    batch[list.get(i)] = new ArrayList<>();
                batch[list.get(i)].add(i);
            }


            double vehiclesUsageCost = 0;
            double travelTimeCost = 0;
            double penaltyCost = 0;
            double cumulativeProcessTime = 0;

            Vertex depot = graph.getVertexById(depotId);

            if (IS_DEBUG_MODE) {
                if (list.toString().equals("[6, 6, 6, 6, 6, 6, 6, 6]")) {
                    list = list;
                }
                if (list.toString().equals("[4, 7, 6, 3, 0, 8, 5, 1, 2]")) {
                    list = list;
                }

                System.out.println("-------------");
                System.out.println(this);
            }

            for (int i=0 ; i<batch.length; i++){
                if (batch[i] == null) continue;

                if (batch[i].size() > depot.capacity) {
                    this.cost = GlobalVars.INF;
                    this.isCostCalculated = true;
                    return cost;
                }

                orderThem(batch[i]);
                batch[i].add(depotId);

                for (int j=0 ; j<batch[i].size() ; j++){
                    int vId = batch[i].get(j);
                    Vertex v = graph.getVertexById(vId);
                    cumulativeProcessTime += v.processTime;
                }

                SimpleTSP tsp = new SimpleTSP(graph, batch[i], cumulativeProcessTime);
                tsp.run();

                vehiclesUsageCost += depot.fixedCost;
                travelTimeCost += tsp.travelTime;
                penaltyCost += tsp.penaltyTaken;

            }

            if (IS_DEBUG_MODE) {
                System.out.println("Cost: " + (vehiclesUsageCost + travelTimeCost + penaltyCost));

                if (vehiclesUsageCost + travelTimeCost + penaltyCost < 94.5)
                    System.out.printf("%s ||||| %.2f, %.2f, %.2f, %.2f\n",
                            this.toString(), vehiclesUsageCost, travelTimeCost, penaltyCost,
                            vehiclesUsageCost + travelTimeCost + penaltyCost);
            }

            this.isCostCalculated = true;
            this.cost = vehiclesUsageCost + travelTimeCost + penaltyCost;
            return cost;
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
