package VRP.Algorithms.BranchAndBound;

import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * a branch and bound algorithm for
 * solving vehicle routing problem (VRP)
 */
public class BranchAndBound {
    private Graph graph;

    public int minimumCost;                   // minimum cost we found
    public BBNode bestNode;                   // best node we found

    private PriorityQueue<BBNode> pq;         // use priority queue (min heap) for best first search

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BranchAndBound(Graph graph) {
        this.graph = graph;
        this.minimumCost = Integer.MAX_VALUE;

        // fill the Global variables
        GlobalVars.bbGraph = graph;

        this.pq = new PriorityQueue<>(10, new Comparator<BBNode>() {
            @Override
            public int compare(BBNode u, BBNode v) {
                return Integer.compare(u.getCost(), v.getCost());
            }
        });
    }


    /**
     * runs the algorithm given the depot name
     *
     * @param depotName is name of the depot (node that contains vehicles)
     */
    public void run(String depotName) {
        // add initial node
        Vertex depotVertex = graph.getVertexByName(depotName);
        pq.add(new BBNode(depotVertex, null));

        // go down the tree
        while (!pq.isEmpty()) {
            BBNode u = pq.poll();

            for (Vertex v : u.vertex.neighbours.keySet()) {
                if (v.type == VertexType.DEPOT          // never go from depot to depot
                        && u.vertex.type == VertexType.DEPOT) continue;

                if (v.type == VertexType.DEPOT) {        // if you going to depot just go
                    addNodeToPriorityQueue(new BBNode(v, u));
                }

                if (v.type == VertexType.CUSTOMER) {
                    // pruning criteria
                    if (u.remainedGoods < v.demand) continue;   // check demand criterion
                    if (u.servicedNodes[v.customerId] == true) continue; // check if this node serviced before

                    // make new node
                    BBNode newNode = new BBNode(v, u);
                    addNodeToPriorityQueue(newNode);
                }
            }
        }
    }

    /**
     * add new node to the queue and check some criteria
     *
     * @param newNode node that must be added to the pq.
     */
    void addNodeToPriorityQueue(BBNode newNode) {

        // if this node is an answer
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == GlobalVars.numberOfCustomers
                && newNode.getCost() < minimumCost) {
            bestNode = newNode;
            minimumCost = newNode.getCost();
            return;
        }

        // if this node is a intermediate node add it to the queue.
        if (!canBePruned(newNode)) pq.add(newNode);
    }

    /**
     * @param newNode: node that must be checked
     * @return true of node can be pruned from the tree
     */
    boolean canBePruned(BBNode newNode) {

        // if new Node so far cost is more than minimum cost
        if (newNode.getCost() >= minimumCost)
            return true;

        // if number of vehicles used is more than we have
        if (newNode.vehicleUsed > GlobalVars.numberOfVehicles)
            return true;

        // if can service remained customers with the remained vehicles
        if (newNode.getLowerBoundForNumberOfExtraVehiclesNeeded() > GlobalVars.numberOfVehicles - newNode.vehicleUsed)
            return true;

        // check lower bound
        if (newNode.getCost() + newNode.getLowerBound() >= minimumCost)
            return true;

        // if this node is a terminal node and not reducing the minimum answer throw it out.
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == GlobalVars.numberOfCustomers)
            return true;

        // else
        return false;
    }

    /**
     * print the answer
     */
    public void printTheAnswer() {
        System.out.println("VertexName (arrivalTime, thisVertexPenalty)\n");
        System.out.println(bestNode.getStringPath() + "\n");
        System.out.println(bestNode.getPrintCostDetailsString());
    }
}
