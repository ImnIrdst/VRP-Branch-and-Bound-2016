package Main.Algorithms.SupplyChainScheduling.BeamSearch;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Algorithms.Other.Random;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;
import Main.GlobalVars;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * a branch and bound algorithm for
 * solving vehicle routing problem (VRP)
 */
public class BeamSearch {
    private Graph graph;

    public double minimumCost;                // minimum cost we found
    public BSNode bestNode;                   // best node we found

    private PriorityQueue<BSNode> pq;         // use priority queue (min heap) for best first search
    private ArrayList<BSNode> canBeAddedToPQ; // nodes that can be added to pq in each step

    private int numberOfFinalNodes = 0;

    private double theta = 0.10;              // pruning probability
    private double delta2 = 0.005;               // step size for theta
    private double updateTime = 1000;
    private double bsCheckPoint = 100;


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
        this.timeLimit = Math.exp(theta * graph.getVertices().size() / 8.) * 1000;

        System.out.printf("Time Limit %.1f ", timeLimit / 1000.);
        System.out.println("Theta0: " + theta0);
        // fill the Global variables
        GlobalVars.ppGraph = graph;
        GlobalVars.minimumValue = this.minimumCost;

        this.pq = new PriorityQueue<>(10, new Comparator<BSNode>() {
            @Override
            public int compare(BSNode u, BSNode v) {
                if (v.numberOfServicedCustomers != u.numberOfServicedCustomers)
                    return Double.compare(v.numberOfServicedCustomers, u.numberOfServicedCustomers);
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
        System.out.println(GlobalVars.equalsLine);
        System.out.println("\t\t\t\t\t\t\tBranch and bound algorithm");
        System.out.println(GlobalVars.equalsLine);

        // add initial node
        Vertex depotVertex = graph.getVertexByName(depotName);
        pq.add(new BSNode(depotVertex, null));

        bsCheckPoint = System.currentTimeMillis();

        // go down the tree
        while (!pq.isEmpty()) {
            BSNode u = pq.poll();

            if (getHierarchy(u).equals("Dp -> A -> H -> C -> G -> D -> Dp -> B -> E -> F"))
                u = u;
            if (getIdsHierarchy(u).equals("[8, 4, 7, 1, 2, 6, 3, 0, 5, 8]"))
                System.out.println(u.getPrintCostDetailsString());

            if (canBePruned(u)) continue;
            canBeAddedToPQ = new ArrayList<>();
            for (Vertex v : u.vertex.neighbours.keySet()) {
                if (v.type == VertexType.DEPOT && u.waitingList.size() == 0) continue;
                if (v.type != VertexType.DEPOT && u.servicedNodes[v.id] == true) continue;

                if (getHierarchy(u).equals("Dp -> A -> H -> C -> G -> D -> Dp -> B -> E") && Objects.equals(v.name, "F"))
                    GlobalVars.enableBreakPoints = true;

                GlobalVars.enableBreakPoints = false;
                BSNode newNode = new BSNode(v, u);
                addNodeToPriorityQueue(newNode);
            }
            Collections.sort(canBeAddedToPQ, new Comparator<BSNode>() {
                @Override
                public int compare(BSNode u, BSNode v) {
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

            printProgress();

            if (System.currentTimeMillis() - lastUpdateCheckpoint > timeLimit) {
                System.out.printf("Time Since Last Update: %.1f, Limit: %.1f",
                        (System.currentTimeMillis() - lastUpdateCheckpoint) / 1000., timeLimit);
                break;
            }

            if (numberOfFinalNodes > 1000) break;

        }
    }

    private void printProgress() {
        if (System.currentTimeMillis() - bsCheckPoint > updateTime) {
//            theta = Math.min(1.0, theta - delta2);
            bsCheckPoint = System.currentTimeMillis();
            System.out.printf("Time: %.1fs, Theta: %.2f, Minimum Cost: %.2f, PQSize: %d, Nodes: %d ",
                    (System.currentTimeMillis() - startTime) / 1000.0, theta, minimumCost, pq.size(), GlobalVars.numberOfBranchAndBoundNodes);
            System.out.printf("Time Since Last Update: %.1f, Limit: %.1f, FinalNodes: %d\n",
                    (System.currentTimeMillis() - lastUpdateCheckpoint) / 1000., timeLimit, numberOfFinalNodes);
        }

    }

    /**
     * addCustomer new node to the queue and check some criteria
     *
     * @param newNode node that must be added to the pq.
     */
    void addNodeToPriorityQueue(BSNode newNode) {
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == graph.getCustomersQty()) {
            numberOfFinalNodes++;
        }

        if (canBePruned(newNode)) return;

        // if this node is an answer
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == graph.getCustomersQty()
                && newNode.getCost() <= minimumCost) {
            // System.out.println(getIdsHierarchy(newNode));
            bestNode = newNode;
            minimumCost = newNode.getCost();
            GlobalVars.minimumValue = minimumCost;

            // theta = Math.min(1.0, theta + delta2);
            lastUpdateCheckpoint = System.currentTimeMillis();
            System.out.printf("^^ Time: %.0fs, Theta: %.2f, Minimum Cost: %.2f, PQSize: %d, Nodes: %d\n",
                    (System.currentTimeMillis() - startTime) / 1000.0, theta, minimumCost, pq.size(), GlobalVars.numberOfBranchAndBoundNodes);

            return;
        }



        // if this node is a intermediate node add it to the queue.
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
        if (!pq.isEmpty() && Random.getRandomDoubleInRange(new Random.DRange(0, 1)) < 0.2)
            return true;

        if (newNode.vehicleUsed > GlobalVars.numberOfVehicles)
            return true;
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
//        if (newNode.getCost() + newNode.getLowerBound() > Math.log10(2 + 10 * theta) * minimumCost)
//            return true;

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
            for (int i = 1; i < routeNodes.length - 1; i++) {
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

    public String getHierarchy(BSNode BSNode) {
        String string = BSNode.vertex.name; // String.format("%s(%.2f)", BSNode.vertex.name, BSNode.getCost());
        for (BSNode node = BSNode.parent; node != null; node = node.parent) {
            string = node.vertex.name + " -> " + string;
        }

        return string;
    }

    public String getIdsHierarchy(BSNode BSNode) {
        String string = "" + BSNode.vertex.id; // String.format("%s(%.2f)", BSNode.vertex.name, BSNode.getCost());
        for (BSNode node = BSNode.parent; node != null; node = node.parent) {
            string = node.vertex.id + ", " + string;
        }
        string = "[" + string + "]";

        return string;
    }
}
