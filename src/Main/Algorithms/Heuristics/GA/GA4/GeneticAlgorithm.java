package Main.Algorithms.Heuristics.GA.GA4;

import Main.Algorithms.Other.Random;
import Main.Algorithms.Other.Random.IRange;
import Main.Algorithms.Other.Random.DRange;
import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;

import java.io.PrintStream;
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

    private final double MUTATION_PROBABILITY = 0.10;
    private final double CROSSOVER_PROBABILITY = 0.80;
    private final int TOURNAMENT_SIZE = 4;

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
        this.minimumCost = GlobalVars.INF;
        this.population = new ArrayList<>();

        this.depotId = graph.getDepotId();
        this.printTimeStepSize = GlobalVars.printTimeStepSize;
    }

    /**
     * run The algorithm to the given time
     *
     * @param computeDurationMilliSecond is how much time can be consumed
     */
    public void run(int computeDurationMilliSecond, int maxIterationsNoUpdate, int iterationsLimit) {
        GlobalVars.log.println(GlobalVars.equalsLine);
        GlobalVars.log.println("\t\t\t\t\t\t\t\t\tGenetic algorithm");
        GlobalVars.log.println(GlobalVars.equalsLine);

        startTime = System.currentTimeMillis();
        long printTime = startTime + printTimeStepSize;
        long iterationsNoUpdate = 0;

        initializePopulation();
        while (System.currentTimeMillis() < startTime + computeDurationMilliSecond) {
            List<Chromosome> newPopulation = new ArrayList<>();

            // cross over
            while (newPopulation.size() < 2 * populationSize) {
                Collections.shuffle(population);
                for (int i = 0; i < populationSize; i += 2 * TOURNAMENT_SIZE) {
                    Chromosome c1 = tournament(population, i, i + TOURNAMENT_SIZE);
                    Chromosome c2 = tournament(population, i + TOURNAMENT_SIZE, i + TOURNAMENT_SIZE * 2);

                    if (getRandom0to1() < CROSSOVER_PROBABILITY) {
                        newPopulation.add(crossOver(c1, c2));
                        newPopulation.add(crossOver(c2, c1));
                        chromosomesQty += 2;
                    } else {
                        newPopulation.add(c1);
                        newPopulation.add(c2);
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
                GlobalVars.log.printf("Iteration #%d,\tTime elapsed: %.2fs,\tChromosomesQty: %d,\tMinimum Cost: %.2f\n",
                        iterations, (System.currentTimeMillis() - startTime) / 1000., chromosomesQty, minimumCost);
            }

            if (iterationsNoUpdate > maxIterationsNoUpdate) break;
            if (iterations > iterationsLimit) break;

            iterations++;
            iterationsNoUpdate++;
        }

        finishTime = System.currentTimeMillis();
    }

    /**
     * initializes the population by shuffling the ids of nodes
     */
    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            population.add(getRandomChromosome());
            chromosomesQty++;
        }
    }

    /**
     * returns a chromosome that satisfies the capacity constraint.
     */
    public Chromosome getRandomChromosome() {
        Chromosome newChromosome = new Chromosome();

        List<Integer> vehicles = new ArrayList<>();
        for (int j = 0; j < vehicleQty; j++) vehicles.add(j);

        int[] remainedCapacity = new int[vehicleQty];
        Arrays.fill(remainedCapacity, GlobalVars.depot.capacity);

        for (int i = 0; i < customerQty && vehicles.size() > 0; i++) {
            Collections.shuffle(vehicles);

            int vId;
            while (true) { // peek the vehicle that is not reached its capacity
                vId = vehicles.get(vehicles.size() - 1);
                remainedCapacity[vId]--;
                if (remainedCapacity[vId] >= 0) break;
                else vehicles.remove(vehicles.size() - 1);
            }

            newChromosome.customersVehicle.add(vId);
        }

        for (int i = 0; i < customerQty; i++)
            newChromosome.customersOrder.add(i);
        Collections.shuffle(newChromosome.customersOrder);

        for (int i = 0; i < customerQty; i++) {
            newChromosome.orderAcceptance.add(Random.getRandomIntInRange(new IRange(0, 1)));
        }

        return newChromosome;
    }

    public Chromosome tournament(List<Chromosome> population, int begin, int end) {
        Chromosome bestChromosome = null;
        double bestValue = -GlobalVars.INF - 1e-9;
        for (int i = begin; i < end; i++)
            if (population.get(i).getCost() > bestValue) {
                bestChromosome = population.get(i);
                bestValue = population.get(i).getCost();
            }

        if (bestValue <= -GlobalVars.INF - 1e-9) bestChromosome = population.get(begin);
        return bestChromosome;
    }

    /**
     * perform cross over on two chromosomes
     *
     * @param chromosome1 array of id of the nodes
     * @param chromosome2 array of id of the nodes
     * @return a new chromosome by crossover of two given chromosomes
     */
    public Chromosome crossOver(Chromosome chromosome1, Chromosome chromosome2) {
        Chromosome newChromosome = new Chromosome(chromosome2);

        int p1 = getRandInt(customerQty);
        int p2 = getRandInt(customerQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        int[] remainedCapacity = new int[vehicleQty];
        Arrays.fill(remainedCapacity, GlobalVars.depot.capacity);

        Collections.fill(newChromosome.customersVehicle, -1);
        Collections.fill(newChromosome.customersOrder, -1);
        for (int i = p1; i <= p2; i++) {
            newChromosome.customersVehicle.set(i, chromosome1.customersVehicle.get(i));
            remainedCapacity[chromosome1.customersVehicle.get(i)]--;
        }

        for (int i = 0; i < customerQty; i++) {
            if (newChromosome.customersVehicle.get(i) != -1) continue;

            int vehicleId = chromosome2.customersVehicle.get(i);
            while (remainedCapacity[vehicleId] <= 0) vehicleId = getRandInt(vehicleQty);

            newChromosome.customersVehicle.set(i, vehicleId);
            remainedCapacity[vehicleId]--;
        }

        // cross over the customersOrder
        p1 = getRandInt(customerQty);
        p2 = getRandInt(customerQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        boolean[] markCustomers = new boolean[customerQty];
        for (int i = p1; i <= p2; i++) {
            newChromosome.customersOrder.set(i, chromosome1.customersOrder.get(i));
            markCustomers[chromosome1.customersOrder.get(i)] = true;
        }

        for (int i = 0, j = 0; i < customerQty && j < customerQty; i++) {
            if (p1 <= j && j <= p2) j = p2 + 1;
            if (markCustomers[chromosome2.customersOrder.get(i)]) continue;

            newChromosome.customersOrder.set(j, chromosome2.customersOrder.get(i));
            j++;
        }

        // cross over the orderAcceptance
        p1 = getRandInt(customerQty);
        p2 = getRandInt(customerQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        newChromosome.orderAcceptance = new ArrayList<>(chromosome1.orderAcceptance);
        List<Integer> subList = chromosome2.orderAcceptance.subList(p1, p2+1);
        for(int i=0 ; i<subList.size() ; i++)
            newChromosome.orderAcceptance.set(i+p1, subList.get(i));


        return newChromosome;
    }

    /**
     * perform mutation on a given chromosome
     *
     * @param chromosome array of id of the nodes
     */
    public void mutate(Chromosome chromosome) {
        if (getRandom0to1() > MUTATION_PROBABILITY)
            return;

        int p1 = getRandInt(customerQty);
        int p2 = getRandInt(customerQty);

        if (p1 > p2) {
            int t = p1;
            p1 = p2;
            p2 = t;
        }

        List<Integer> temp = new ArrayList<>();
        for (int k = p1; k <= p2; k++) {
            temp.add(chromosome.customersOrder.get(k));
        }
        Collections.shuffle(temp);

        for (int k = p1; k <= p2; k++) {
            chromosome.customersOrder.set(k, temp.get(k - p1));
        }

        // for the customersVehicle
        p1 = getRandInt(customerQty);
        p2 = getRandInt(customerQty);

        if (p1 > p2) {
            int t = p1;
            p1 = p2;
            p2 = t;
        }

        temp = new ArrayList<>();
        for (int k = p1; k <= p2; k++) {
            temp.add(chromosome.customersVehicle.get(k));
        }
        Collections.shuffle(temp);

        for (int k = p1; k <= p2; k++) {
            chromosome.customersVehicle.set(k, temp.get(k - p1));
        }

        // mutate the orderAcceptance
        p1 = getRandInt(customerQty);
        p2 = getRandInt(customerQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        List<Integer> subList = chromosome.orderAcceptance.subList(p1, p2+1);
        Collections.shuffle(subList);

        chromosome.isCostCalculated = false;
    }


    /**
     * Selects top chromosomes from old population and their children
     */
    public List<Chromosome> selection(List<Chromosome> chromosomes) {
        List<Chromosome> newPopulation = new ArrayList<>();

        // select top nodes
        Collections.sort(chromosomes);

        for (int i = 0; i < 7 * (populationSize / 10); i++) {
            newPopulation.add(chromosomes.get(i));
        }

        for (int i = chromosomes.size() - (3 * (populationSize / 10)); i < chromosomes.size(); i++) {
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

    public String bestChromosomeString() {
        return ("Best Chromosome: " + bestChromosome
                + ", " + String.format("Cost: %.2f", minimumCost)
                + ", " + String.format("iterations: %d", iterations));
    }

    public String bestChromosomeCostDetailsString() {
        return String.format("travelCost = %.1f; penaltyCost = %.1f; maxGainCost = %.1f; vehicleUsageCost = %.1f;",
                bestChromosome.travelCost, bestChromosome.penaltyCost, bestChromosome.maxGainCost, bestChromosome.vehicleUsageCost);
    }


    /**
     * @return a random number less than given bound
     */
    public int getRandInt(int bound) {
        return Random.getRandomIntInRange(new IRange(0, bound-1));
    }

    /**
     * @return a random number between 0, 1 for probability
     */
    public double getRandom0to1() {
        return getRandInt(1001) / 1000.;
    }

    /**
     * Chromosome class for a setCustomer of ids (for VRPD) problem
     */
    private class Chromosome implements Comparable<Chromosome> {
        public List<Integer> customersOrder;
        public List<Integer> customersVehicle;
        public List<Integer> orderAcceptance;

        private double cost;
        private double travelCost;
        private double penaltyCost;
        private double vehicleUsageCost;
        private double maxGainCost;

        private boolean isCostCalculated = false;

        /**
         * default constructor
         */
        public Chromosome() {
            customersOrder = new ArrayList<>();
            customersVehicle = new ArrayList<>();
            orderAcceptance = new ArrayList<>();
        }

        /**
         * copy constructor
         */
        public Chromosome(Chromosome chromosome) {
            this.customersOrder = new ArrayList<>(chromosome.customersOrder);
            this.customersVehicle = new ArrayList<>(chromosome.customersVehicle);
            this.orderAcceptance = new ArrayList<>(chromosome.orderAcceptance);
        }

        /**
         * fitness function for this chromosome
         */
        public double getCost() {
            if (isCostCalculated == true)
                return cost;

            travelCost = 0;
            penaltyCost = 0;
            maxGainCost = 0;
            vehicleUsageCost = 0;

            List<Integer>[] batch = new ArrayList[vehicleQty];
            for (int i = 0; i < customersVehicle.size(); i++) {
                if (batch[customersVehicle.get(i)] == null) {
                    batch[customersVehicle.get(i)] = new ArrayList<>();
                }
                if (orderAcceptance.get(i) == 1) {
                    batch[customersVehicle.get(i)].add(customersOrder.get(i));
                    Vertex v = graph.getVertexById(customersOrder.get(i));
                    maxGainCost += v.maximumGain;
                }
            }

            if (this.toString().equals("[1, 1, 1, 1, 1, 1, 1, 1] [1, 1, 1, 1, 1, 1, 1, 1]")) {
                customersVehicle = customersVehicle;
            }

            double cumulativeProcessTime = 0;
            Vertex depot = graph.getVertexById(depotId);

            for (int i = 0; i < batch.length; i++) {
                if (batch[i] == null) continue;
                if (batch[i].size() == 0) continue;

                if (batch[i].size() > depot.capacity) {
                    this.cost = GlobalVars.INF;
                    this.isCostCalculated = true;
                    return cost;
                }

                batch[i].add(depotId);
                for (int j = 0; j < batch[i].size(); j++) {
                    int vId = batch[i].get(j);
                    Vertex v = graph.getVertexById(vId);
                    cumulativeProcessTime += v.processTime;
                }

                SimpleTSP tsp = new SimpleTSP(graph, batch[i], cumulativeProcessTime);
                tsp.run();

                vehicleUsageCost -= depot.fixedCost;
                travelCost -= tsp.travelTime;
                penaltyCost -= tsp.penaltyTaken;
            }

            this.isCostCalculated = true;
            this.cost = maxGainCost + penaltyCost + vehicleUsageCost + travelCost;
            return cost;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(o.getCost(), this.getCost());
        }

        @Override
        public String toString() {
            return customersOrder.toString() + " " +
                    customersVehicle.toString() + " " +
                    orderAcceptance.toString();
        }

        @Override
        public int hashCode() {
            return customersOrder.hashCode();
        }
    }
}

// ----------------- Debuging ------------------
//if (IS_DEBUG_MODE) {
//        if (customers.toString().equals("[6, 6, 6, 6, 6, 6, 6, 6]")) {
//        customers = customers;
//        }
//        if (customers.toString().equals("[4, 7, 6, 3, 0, 8, 5, 1, 2]")) {
//        customers = customers;
//        }
//
//        GlobalVars.log.println("-------------");
//        GlobalVars.log.println(this);
//        }
//if (IS_DEBUG_MODE) {
//        GlobalVars.log.println("Cost: " + (vehiclesUsageCost + travelTimeCost + penaltyCost));
//
//        if (vehiclesUsageCost + travelTimeCost + penaltyCost < 94.5)
//        GlobalVars.log.printf("%s ||||| %.2f, %.2f, %.2f, %.2f\n",
//        this.toString(), vehiclesUsageCost, travelTimeCost, penaltyCost,
//        vehiclesUsageCost + travelTimeCost + penaltyCost);
//        }

// -------------------- Trash Code ---------------
//    /**
//     * perform mutation on a given chromosome
//     *
//     * @param chromosome array of id of the nodes
//     */
//    public void mutate1(Chromosome chromosome) {
//        if (getRandom0to1() > MUTATION_PROBABILITY)
//            return;
//
//        int i = getRandInt(chromosome.customersSize);
//        int j = getRandInt(chromosome.customersSize);
//
//        // swap
//        int tmp = chromosome.getCustomer(i);
//        chromosome.setCustomer(i, chromosome.getCustomer(j));
//        chromosome.setCustomer(j, tmp);
//    }