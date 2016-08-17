package VRP.Algorithms.BeamSearch;

import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private double theta = 0.20;              // pruning probability
    private double thetaStep = 0.1;           // step size for theta
    private double updateTime = 1000;

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BeamSearch(Graph graph, double upperBound) {
        this.graph = graph;
        this.minimumCost = upperBound;

        // fill the Global variables
        GlobalVars.ppGraph = graph;
        GlobalVars.minimumValue = this.minimumCost;

        this.pq = new PriorityQueue<>(10, new Comparator<BSNode>() {
            @Override
            public int compare(BSNode u, BSNode v) {
                return Double.compare(u.getCost() + u.getLowerBound(), v.getCost() + v.getLowerBound());
            }
        });
    }


    /**
     * runs the algorithm given the depot name
     *
     * @param depotName is name of the depot (node that contains vehicles)
     */
    public void run(String depotName) {
//        System.out.println("--------------------------");
//        System.out.println("Branch and bound algorithm");
//        System.out.println("--------------------------");

        // add initial node
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
                        && v.type == VertexType.CUSTOMER && v.hasVehicle == 0) continue;

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
                    return Double.compare(u.getCost() + u.getLowerBound(), v.getCost() + v.getLowerBound());
                }
            });

            if (canBeAddedToPQ.size() < 1) continue;
            for (int i = 0; i < Math.max(1, theta * (canBeAddedToPQ.size())); i++) {
                pq.add(canBeAddedToPQ.get(i));
            }

            if (System.currentTimeMillis() - bsCheckPoint > updateTime) {
                System.out.printf("Theta: %.2f, Minimum Cost: %.2f\n", theta, minimumCost);
                theta -= (0.1) * thetaStep;
                bsCheckPoint = System.currentTimeMillis();
            }

        }
    }

    /**
     * add new node to the queue and check some criteria
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
            theta -= thetaStep;
            System.out.printf("Theta: %.2f, Minimum Cost: %.2f\n", theta, minimumCost);
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
        if (newNode.getCost() + newNode.getLowerBound() > (theta * 10) * minimumCost)
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
