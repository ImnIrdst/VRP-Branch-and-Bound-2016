package Main.Algorithms.SupplyChainScheduling.BeamSearch;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * a branch and bound algorithm for
 * solving vehicle routing problem (VRP)
 */
public class BeamSearch {
    private Graph graph;

    public double minimumCost;                // minimum cost we found
    public BSNode bestNode;                   // best node we found

    //    private Stack<BSNode> stack;
    private PriorityQueue<BSNode> pq;         // use priority queue (min heap) for best first search
    private ArrayList<BSNode> canBeAddedToPQ; // nodes that can be added to pq in each step

    private double theta = 0.10;              // pruning probability
//    private double delta1 = 0.01;              // for time
    private double delta2 = 0.005;               // step customersSize for theta
    private double updateTime = 1000;

    private double timeLimit;
    private long lastUpdateCheckpoint = (long) 1e18;
    private long startTime = 0;

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BeamSearch(Graph graph, double theta0, double upperBound) {
        this.graph = graph;
        this.theta = theta0;
        this.minimumCost = upperBound;
        this.startTime = System.currentTimeMillis();
        this.timeLimit = Math.exp(theta*graph.getVertices().size()/8.) * 1000;

        System.out.printf("%.2f", timeLimit/1000.);

        System.out.println("Theta0: " + theta0);
        // fill the Global variables
        GlobalVars.ppGraph = graph;
        GlobalVars.minimumValue = this.minimumCost;


//        this.stack = new Stack<>();
        this.pq = new PriorityQueue<>(10, new Comparator<BSNode>() {
            @Override
            public int compare(BSNode u, BSNode v) {
                if (v.numberOfServicedCustomers != u.numberOfServicedCustomers)
                    return Double.compare(v.numberOfServicedCustomers, u.numberOfServicedCustomers);
//                if (u.cumulativePenaltyTaken != v.cumulativePenaltyTaken)
//                    return Double.compare(u.cumulativePenaltyTaken, v.cumulativePenaltyTaken);
                return Double.compare(u.getCost() + u.getLowerBound(), v.getCost() + v.getLowerBound());

            }
        });
    }


    /**
     * runs the algorithm given the depot name
     *
     * @param depotName is name of the depot (node that contains customersVehicle)
     */
    public void run(String depotName) {
//        System.out.println("--------------------------");
//        System.out.println("Branch and bound algorithm");
//        System.out.println("--------------------------");

        // addCustomer initial node
        Vertex depotVertex = graph.getVertexByName(depotName);
        pq.add(new BSNode(depotVertex, null));
        long bsCheckPoint = System.currentTimeMillis();

        // go down the tree
        while (!pq.isEmpty()) {
            BSNode u = pq.poll();
            if (canBePruned(u)) continue;
            canBeAddedToPQ = new ArrayList<>();
            for (Vertex v : u.vertex.neighbours.keySet()) {

                if (v.id == u.vertex.id) continue;         // never go from node to itself
                if (v.type == VertexType.DEPOT) {        // if you going to depot just go
                    addNodeToPriorityQueue(new BSNode(v, u));
                    continue;
                }

                if (u.vertex.type == VertexType.DEPOT
                        && v.type == VertexType.CUSTOMER) continue;

                if (v.type == VertexType.CUSTOMER) {
                    // pruning criteria
                    if (u.servicedNodes[v.getId()] == true) continue; // check if this node serviced before

                    // make new node
                    BSNode newNode = new BSNode(v, u);
                    addNodeToPriorityQueue(newNode);
                }
            }

            Collections.sort(canBeAddedToPQ, new Comparator<BSNode>() {
                @Override
                public int compare(BSNode u, BSNode v) {
//                    if (v.availableCapacity != u.availableCapacity)
//                        return Integer.compare(v.availableCapacity, u.availableCapacity);
//                    else
                    if (v.remainedCapacity != u.remainedCapacity)
                        return Integer.compare(v.remainedCapacity, u.remainedCapacity);
                    else
                        return Double.compare(u.getCost() + u.getLowerBound(), v.getCost() + v.getLowerBound());
                }
            });

            if (canBeAddedToPQ.size() < 1) continue;

            if (pq.size() > (int) 1e6)
                pq.add(canBeAddedToPQ.get(0));
            else for (int i = 0; i < Math.max(1, theta * (canBeAddedToPQ.size())); i++)
                pq.add(canBeAddedToPQ.get(i));

            if (System.currentTimeMillis() - bsCheckPoint > updateTime) {
//                theta = Math.min(1.0, theta + delta2);
                bsCheckPoint = System.currentTimeMillis();
                System.out.printf("Time: %.1fs, Theta: %.2f, Minimum Cost: %.2f, PQSize: %d, Nodes: %d ",
                        (System.currentTimeMillis() - startTime) / 1000.0, theta, minimumCost, pq.size(), GlobalVars.numberOfBranchAndBoundNodes);
                System.out.printf("Time Since Last Update: %.1f, Limit: %.1f\n",
                        (System.currentTimeMillis() - lastUpdateCheckpoint) / 1000., timeLimit);
                System.out.println(u.numberOfServicedCustomers / (double) u.servicedNodes.length);
            }

            if (System.currentTimeMillis() - lastUpdateCheckpoint > timeLimit) {
                System.out.printf("Time Since Last Update: %.1f, Limit: %.1f",
                        (System.currentTimeMillis() - lastUpdateCheckpoint) / 1000., timeLimit);
                break;
            }

        }
    }

    /**
     * addCustomer new node to the queue and check some criteria
     *
     * @param newNode node that must be added to the pq.
     */
    void addNodeToPriorityQueue(BSNode newNode) {

        // if this node is an answer
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == GlobalVars.numberOfCustomers
                && newNode.getCost() <= minimumCost) {
            bestNode = newNode;
            minimumCost = newNode.getCost();
            GlobalVars.minimumValue = minimumCost;
            theta = Math.min(1.0, theta + delta2);
            lastUpdateCheckpoint = System.currentTimeMillis();
            System.out.printf("^^ Time: %.0fs, Theta: %.2f, Minimum Cost: %.2f, PQSize: %d, Nodes: %d\n",
                    (System.currentTimeMillis() - startTime) / 1000.0, theta, minimumCost, pq.size(), GlobalVars.numberOfBranchAndBoundNodes);

            return;
        }

        // if this node is a intermediate node addCustomer it to the queue.
        if (!canBePruned(newNode)) {
            canBeAddedToPQ.add(newNode);
            GlobalVars.numberOfBranchAndBoundNodes++;
        }
    }

    /**
     * @param newNode: node that must be checked
     * @return true of node can be pruned from the tree
     */
    boolean canBePruned(BSNode newNode) {
//        Random random = new Random();
//
//        if (lastUpdateCheckpoint < (long)1e17 &&
//                random.nextInt(100)/100. < newNode.numberOfServicedCustomers / (double)newNode.servicedNodes.length + 0.5)
//            return true;

        // if new node capacity is negative
        if (newNode.remainedCapacity < 0)
            return true;

        // if new Node so far cost is more than minimum cost
        if (newNode.getCost() > minimumCost)
            return true;

        // check lower bound
        if (newNode.getCost() + newNode.getLowerBound() > minimumCost)
            return true;

        // check lower bound
        if (newNode.getCost() + newNode.getLowerBound() > Math.log10(2 + 10 * theta) * minimumCost)
            return true;

        // else
        return false;
    }

    /**
     * generates a csv file that contains the wtk info of result routes
     *
     * @param folderPath: the path of the folder that file must be save there
     * @param dijkstra:   Dijkstra algorithm on original graph
     */
    public void exportTheResultWTK(String folderPath, Dijkstra dijkstra) throws FileNotFoundException {
        String[] routes = bestNode.getStringPath().split("\n");

        for (int j = 0; j < routes.length; j++) {
            String filePath = folderPath + "route" + j + ".csv";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);

            printWriter.println("OBJECTID;wrk");
            String[] routeNodes = routes[j].split("->");
            for (int i = 0; i < routeNodes.length - 1; i++) {
                String[] edgesWTKs = dijkstra.getTheShortestPathEdgesWTKStringBetweenTwoNodes(
                        routeNodes[i].trim().split(" ")[0], routeNodes[i + 1].trim().split(" ")[0]
                ).split("\n");

                for (int k = 0; k < edgesWTKs.length; k++) {
                    printWriter.println(k + ";" + edgesWTKs[k]);
                }
            }
            printWriter.close();
        }
    }

    /**
     * print the answer
     */
    public void printTheAnswer() {
        System.out.println();
        System.out.println("Format -> VertexName (arrivalTime, thisVertexPenalty, vertex.dueDate, vertex.demand, vertex.capacity, vertex.hasVehicle)");
        System.out.println(bestNode.getStringPath() + "\n");
        System.out.println(bestNode.getPrintCostDetailsString());
    }
}
