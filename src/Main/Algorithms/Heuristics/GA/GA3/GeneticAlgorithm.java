package Main.Algorithms.Heuristics.GA.GA3;

import Main.Algorithms.Heuristics.DispatchingRules.RankingIndex;
import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;

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
    private double totalProcessTimes;
    private Chromosome bestChromosome;
    private List<Chromosome> population;

    private final double MUTATION_PROBABILITY = 0.10;
    private final double CROSSOVER_PROBABILITY = 0.80;
    private final int TOURNAMENT_SIZE = 4;

    private final int NUMBER_OF_DISPATCHING_RULES = RankingIndex.NUMBER_OF_RULES;
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

        for (Vertex v : graph.getCustomerVertices()) totalProcessTimes += v.processTime;
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
        List<Integer> vehicles = new ArrayList<>();
        for (int j = 0; j < vehicleQty; j++) vehicles.add(j);

        Chromosome newChromosome = new Chromosome();
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

            newChromosome.addCustomer(vId);
        }

        for (int i = 0; i < vehicleQty; i++)
            newChromosome.addVehicleRule(getRandInt(NUMBER_OF_DISPATCHING_RULES) + 1);

        return newChromosome;
    }

    public Chromosome tournament(List<Chromosome> population, int begin, int end) {
        Chromosome bestChromosome = null;
        double bestValue = GlobalVars.INF;
        for (int i = begin; i < end; i++)
            if (population.get(i).getCost() < bestValue) bestChromosome = population.get(i);

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

        int p1 = getRandInt(customerQty);
        int p2 = getRandInt(customerQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        Chromosome newChromosome = new Chromosome();
        int[] remainedCapacity = new int[vehicleQty];
        Arrays.fill(remainedCapacity, GlobalVars.depot.capacity);

        newChromosome = new Chromosome(chromosome2);
        Collections.fill(newChromosome.customers, -1);

        for (int i = p1; i <= p2; i++) {
            newChromosome.setCustomer(i, chromosome1.getCustomer(i));
            remainedCapacity[chromosome1.getCustomer(i)]--;
        }

        for (int i = 0; i < customerQty; i++) {
            if (newChromosome.getCustomer(i) != -1) continue;

            int vehicleId = chromosome2.getCustomer(i);
            while (remainedCapacity[vehicleId] <= 0) vehicleId = getRandInt(vehicleQty);

            newChromosome.setCustomer(i, vehicleId);
            remainedCapacity[vehicleId]--;
        }

        // cross over the vehicle rules
        p1 = getRandInt(vehicleQty);
        p2 = getRandInt(vehicleQty);

        // swap
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }

        for (int i = p1; i <= p2; i++)
            newChromosome.vehiclesRules.set(i, chromosome1.getVehicleRule(i));

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
            temp.add(chromosome.getCustomer(k));
        }
        Collections.shuffle(temp);

        for (int k = p1; k <= p2; k++) {
            chromosome.setCustomer(k, temp.get(k - p1));
        }

        // for the vehicle rules
        p1 = getRandInt(vehicleQty);
        p2 = getRandInt(vehicleQty);

        if (p1 > p2) {
            int t = p1;
            p1 = p2;
            p2 = t;
        }

        temp = new ArrayList<>();
        for (int k = p1; k <= p2; k++) {
            temp.add(chromosome.getVehicleRule(k));
        }
        Collections.shuffle(temp);

        for (int k = p1; k <= p2; k++) {
            chromosome.setVehicleRule(k, temp.get(k - p1));
        }

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

    public void printBestChromosome() {
        System.out.println("Best Chromosome: " + bestChromosome
                + ", " + String.format("Cost: %.2f", minimumCost)
                + ", " + String.format("Cost: %d", iterations));
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
     * Chromosome class for a setCustomer of ids (for VRPD) problem
     */
    private class Chromosome implements Comparable<Chromosome> {
        public List<Integer> customers;
        public List<Integer> vehiclesRules;

        private double cost;
        private double travelCost;
        private double penaltyCost;
        private double vehicleUsageCost;

        private boolean isCostCalculated = false;

        /**
         * default constructor
         */
        public Chromosome() {
            customers = new ArrayList<>();
            vehiclesRules = new ArrayList<>();
        }

        /**
         * copy constructor
         */
        public Chromosome(Chromosome chromosome) {
            this.customers = new ArrayList<>(chromosome.customers);
            this.vehiclesRules = new ArrayList<>(chromosome.vehiclesRules);
        }

        /**
         * get Customer id in idx position
         */
        public int getCustomer(int idx) {
            return customers.get(idx);
        }

        /**
         * get rule for vehicle in idx position
         */
        public int getVehicleRule(int idx) {
            return vehiclesRules.get(idx);
        }

        /**
         * set customer in idx position
         */
        public void setCustomer(int idx, int value) {
            customers.set(idx, value);
        }

        /**
         * set rule for vehicle in idx position
         */
        public void setVehicleRule(int idx, int value) {
            vehiclesRules.set(idx, value);
        }

        /**
         * adds the value to the end of customers
         */
        public void addCustomer(int value) {
            customers.add(value);
        }

        /**
         * add the rule to the end of customersVehicle
         */
        public void addVehicleRule(int rule) {
            vehiclesRules.add(rule);
        }

        /**
         * randomly order them.
         */
        List<Integer> orderThem(List<Integer> customers) {
            Collections.shuffle(customers);

            return customers;
        }

        /**
         * order them by dispatching rule 2 (Khodabandeh's rule)
         */
        List<Integer> orderThem1(List<Integer> customers, double previousProcessTimes, int indexType) {
            int servedNodesQty = 0;
            boolean[] isServed = new boolean[customers.size()];

            double sumOfProcessTimes = totalProcessTimes;

            double thisBatchProcessTimes = 0;
            for (Integer vId : customers)
                thisBatchProcessTimes += graph.getVertexById(vId).processTime;

            double arrivalTime = previousProcessTimes + thisBatchProcessTimes;

            Vertex u = graph.getDepot();
            List<Integer> orderedCustomers = new ArrayList<>();
            while (servedNodesQty < customers.size()) {

                int nextId = -1;
                Vertex next = null;
                double bestValue = -GlobalVars.INF;
                for (int i = 0; i < customers.size(); i++) {
                    if (isServed[i]) continue;
                    Vertex v = graph.getVertexById(customers.get(i));

                    double indexValue = -GlobalVars.INF;

                    switch (indexType) {
                        case 1: // (DueDates)
                            indexValue = RankingIndex.getIndexValue1(u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
                            break;
                        case 2: // (Khodabandeh's rule)
                            indexValue = RankingIndex.getIndexValue2(u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
                            break;
                        case 3: // (Pinedo ATC rule)
                            indexValue = RankingIndex.getIndexValue3(u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
                            break;
                        case 4: // (Process Times)
                            indexValue = RankingIndex.getIndexValue4(u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
                            break;
                    }

                    if (indexValue > bestValue) {
                        bestValue = indexValue;
                        next = v;
                        nextId = i;
                    }
                }

                orderedCustomers.add(next.id);
                servedNodesQty++;
                isServed[nextId] = true;
                u = next;
            }

            return orderedCustomers;
        }

        /**
         * fitness function for this chromosome
         */
        public double getCost() {
            if (isCostCalculated == true)
                return cost;

            List<Integer>[] batch = new ArrayList[vehicleQty];
            for (int i = 0; i < customers.size(); i++) {
                if (batch[customers.get(i)] == null)
                    batch[customers.get(i)] = new ArrayList<>();
                batch[customers.get(i)].add(i);
            }

            vehicleUsageCost = 0;
            travelCost = 0;
            penaltyCost = 0;

            double cumulativeProcessTime = 0;
            Vertex depot = graph.getVertexById(depotId);

            for (int i = 0; i < batch.length; i++) {
                if (batch[i] == null) continue;

                if (batch[i].size() > depot.capacity) {
                    this.cost = GlobalVars.INF;
                    this.isCostCalculated = true;
                    return cost;
                }
                // batch[i] = orderThem(batch[i]);
                batch[i] = orderThem1(batch[i], cumulativeProcessTime, vehiclesRules.get(i));

                batch[i].add(depotId);
                for (int j = 0; j < batch[i].size(); j++) {
                    int vId = batch[i].get(j);
                    Vertex v = graph.getVertexById(vId);
                    cumulativeProcessTime += v.processTime;
                }

                SimpleTSP tsp = new SimpleTSP(graph, batch[i], cumulativeProcessTime);
                tsp.run();

                vehicleUsageCost += depot.fixedCost;
                travelCost += tsp.travelTime;
                penaltyCost += tsp.penaltyTaken;
            }

            this.isCostCalculated = true;
            this.cost = vehicleUsageCost + travelCost + penaltyCost;
            return cost;
        }

        @Override
        public int compareTo(Chromosome o) {
            return Double.compare(this.getCost(), o.getCost());
        }

        @Override
        public String toString() {
            return customers.toString() + " " + vehiclesRules.toString();
        }

        @Override
        public int hashCode() {
            return customers.hashCode();
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
//        System.out.println("-------------");
//        System.out.println(this);
//        }
//if (IS_DEBUG_MODE) {
//        System.out.println("Cost: " + (vehiclesUsageCost + travelTimeCost + penaltyCost));
//
//        if (vehiclesUsageCost + travelTimeCost + penaltyCost < 94.5)
//        System.out.printf("%s ||||| %.2f, %.2f, %.2f, %.2f\n",
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