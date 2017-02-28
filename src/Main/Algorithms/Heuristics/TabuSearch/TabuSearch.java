package Main.Algorithms.Heuristics.TabuSearch;

import Main.Algorithms.Other.Random;
import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;

import java.util.*;

/**
 * Created by iman on 2/28/17.
 */
public class TabuSearch {

    private Graph graph;
    private int vehicleQty;
    private int customerQty;
    private int depotId;

    public double maximumCost;
    public Chromosome bestChromosome;

    public long iterations = 0;

    private long startTime = 0;
    private long finishTime = 0;
    private long printTimeStepSize;

    private final boolean IS_VERBOSE = true;
    private final boolean IS_DEBUG_MODE = false;
    private Chromosome initChromosome;

    public TabuSearch(Graph graph, int customerQty, int vehicleQty) {
        this.graph = graph;
        this.vehicleQty = vehicleQty;
        this.customerQty = customerQty;

        this.depotId = graph.getDepotId();

        this.maximumCost = -GlobalVars.INF;
    }

    public void runUsingConfigFile() {
        this.run(
                TabuSearchConfigs.MAX_ITERATIONS,
                TabuSearchConfigs.MAX_TABU_LIST_SIZE
        );
    }

    public void run(int maxIterations, int maxTabuListSize) {
        this.startTime = System.currentTimeMillis();

        TabuList tabuList = new TabuList(maxTabuListSize);
        Chromosome currentChromosome = getInitChromosome();

        for (int iteration = 0; iteration < maxIterations; iteration++) {

//            System.out.println(currentChromosome + " ||| " + currentChromosome.detailedCostString());

            double bestNextCost = -GlobalVars.INF;
            Chromosome bestNextChromosome = null;
            TabuListEntry bestNextMove = null;

            int k = Random.getRandomIntInRange(new Random.IRange(0, 2));

            // for customer order
            if (k == 0) {

                for (int i = 0; i < customerQty; i++) {
                    for (int j = i + 1; j < customerQty; j++) {

                        TabuListEntry tabuListEntry = new TabuListEntry(i, j, k);
                        TabuListEntry revTabuListEntry = new TabuListEntry(j, i, k);

                        if (tabuList.contains(tabuListEntry))
                            continue;

                        Chromosome next = new Chromosome(currentChromosome);

                        int temp = next.customersOrder.get(i);
                        next.customersOrder.set(i, next.customersOrder.get(j));
                        next.customersOrder.set(j, temp);

                        if (next.getCost() > bestNextCost) {
                            bestNextCost = next.getCost();
                            bestNextChromosome = next;
                            bestNextMove = revTabuListEntry;
                        }
                    }
                }

            }
            // for customer vehicle
            if (k == 1) {

                for (int i = 0; i < customerQty; i++) {
                    for (int j = 0; j < vehicleQty; j++) {

                        if (currentChromosome.customersVehicle.get(i) == j)
                            continue;

                        TabuListEntry tabuListEntry = new TabuListEntry(i, j, k);
                        TabuListEntry revTabuListEntry = new TabuListEntry(i, currentChromosome.customersVehicle.get(i), k);

                        if (tabuList.contains(tabuListEntry))
                            continue;

                        Chromosome next = new Chromosome(currentChromosome);

                        next.customersVehicle.set(i, j);

                        if (next.getCost() > bestNextCost) {
                            bestNextCost = next.getCost();
                            bestNextChromosome = next;
                            bestNextMove = revTabuListEntry;
                        }
                    }
                }

            }

            // for order acceptance
            if (k == 2) {

                for (int i = 0; i < customerQty; i++) {
                    for (int j = 0; j < 2; j++) { // zero one

                        if (currentChromosome.orderAcceptance.get(i) == j)
                            continue;

                        TabuListEntry tabuListEntry = new TabuListEntry(i, j, k);
                        TabuListEntry revTabuListEntry = new TabuListEntry(i, currentChromosome.orderAcceptance.get(i), k);

                        if (tabuList.contains(tabuListEntry))
                            continue;

                        Chromosome next = new Chromosome(currentChromosome);

                        next.orderAcceptance.set(i, j);

                        if (next.getCost() > bestNextCost) {
                            bestNextCost = next.getCost();
                            bestNextChromosome = next;
                            bestNextMove = revTabuListEntry;
                        }
                    }
                }

            }

            if (bestNextMove == null)
                continue;

            tabuList.insert(bestNextMove);
            currentChromosome = bestNextChromosome;
            if (bestNextCost > maximumCost) {
                maximumCost = bestNextCost;
                bestChromosome = bestNextChromosome;
            }

            this.iterations = iteration;
        }

        this.finishTime = System.currentTimeMillis();
    }

    public Chromosome getInitChromosome() {
        Chromosome newChromosome = new Chromosome();

        for (int i = 0; i < customerQty; i++) {
            newChromosome.customersOrder.add(i);
            newChromosome.customersVehicle.add(Random.getRandomIntInRange(new Random.IRange(0, vehicleQty - 1)));
            newChromosome.orderAcceptance.add(Random.getRandomIntInRange(new Random.IRange(0, 1)));
        }

        return newChromosome;
    }

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
            newChromosome.orderAcceptance.add(Random.getRandomIntInRange(new Random.IRange(0, 1)));
        }

        // System.out.printf("%s, %.1f\n", newChromosome, newChromosome.cost);
        return newChromosome;
    }

    public double getElapsedTimeInSeconds() {
        return (finishTime - startTime) / 1000.0;
    }

    private class TabuList {

        int maxSize;
        LinkedList<TabuListEntry> list;

        public TabuList(int maxSize) {
            this.maxSize = maxSize;
            this.list = new LinkedList<>();
        }

        public boolean contains(TabuListEntry entry) {
            for (TabuListEntry item : list) {
                if (item.equals(entry)) return true;
            }
            return false;
        }

        public void insert(TabuListEntry entry) {
            list.add(entry);
            if (list.size() > maxSize)
                list.removeFirst();
        }

    }

    private class TabuListEntry {
        int i, j, k;

        public TabuListEntry(int i, int j, int k) {
            this.i = i;
            this.j = j;
            this.k = k;
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }

        @Override
        public String toString() {
            return String.format("(%d, %d, %d)", i, j, k);
        }

        @Override
        public boolean equals(Object o) {
            return this.hashCode() == o.hashCode();
        }
    }

    /**
     * Chromosome class for a setCustomer of ids (for VRPD) problem
     */
    public class Chromosome implements Comparable<Chromosome> {
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

            if (this.orderAcceptance.toString().equals("[0, 1, 1, 0]")) {
                customersVehicle = customersVehicle;
            }

            List<Integer>[] batch = new ArrayList[vehicleQty];
            for (int i = 0; i < customersVehicle.size(); i++) {
                if (orderAcceptance.get(customersOrder.get(i)) == 1) {
                    Vertex v = graph.getVertexById(customersOrder.get(i));
                    maxGainCost += v.maximumGain;

                    if (batch[customersVehicle.get(v.getId())] == null) {
                        batch[customersVehicle.get(v.getId())] = new ArrayList<>();
                    }
                    batch[customersVehicle.get(v.getId())].add(v.getId());
                }
            }


            double cumulativeProcessTime = 0;
            Vertex depot = graph.getVertexById(depotId);

            for (int i = 0; i < batch.length; i++) {
                if (batch[i] == null) continue;
                if (batch[i].size() == 0) continue;

                if (batch[i].size() > depot.capacity) {
                    this.cost = -GlobalVars.INF;
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
                    orderAcceptance.toString() + " " +
                    String.format("%.2f", getCost());
        }

        public String detailedCostString() {
            return String.format("travelCost = %.1f; penaltyCost = %.1f; maxGainCost = %.1f; vehicleUsageCost = %.1f;",
                    travelCost, penaltyCost, maxGainCost, vehicleUsageCost);
        }

        @Override
        public int hashCode() {
            return customersOrder.hashCode();
        }
    }
}
