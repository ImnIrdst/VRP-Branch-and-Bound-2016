package VRP.Algorithms.BranchAndBound;

import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * a branch and bound algorithm for solving vehicle routing problem (VRP)
 */
public class BranchAndBound {
    public Map<String, Vertex> adjacencyList; // the adjacencyList of the graph (provided by user)

    public int minimumCost;                   // minimum cost we found
    public BBNode bestNode;                   // best node we found

    private PriorityQueue<BBNode> pq;         // use priority queue (min heap) for best first search

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BranchAndBound(Graph graph) {
        this.adjacencyList = graph.adjacencyList;
        this.minimumCost = Integer.MAX_VALUE;


        this.pq = new PriorityQueue<>(10, new Comparator<BBNode>() {
            @Override
            public int compare(BBNode u, BBNode v) {
                return Integer.compare(u.cost(), v.cost());
            }
        });
    }

    /**
     * returns distance between to node (for simplicity)
     *
     * @param u beginning node
     * @param v end node
     * @return distance of the two nodes
     */
    public int getDistance(Vertex u, Vertex v) {
        if (u.type == VertexType.DEPOT && v.type == VertexType.DEPOT) {
            return Integer.MAX_VALUE; // there is no way from
        } else {
            return adjacencyList.get(u.name).neighbours.get(v);
        }
    }

    /**
     * runs the algorithm given the depot name
     *
     * @param depotName is name of the depot (node that contains vehicles)
     */
    public void run(String depotName) {
        // add initial node
        pq.add(new BBNode(adjacencyList.get(depotName), 0, 0, null));

        // go down the tree
        int addedTime = 0;
        int addedPenalty = 0;
        while (!pq.isEmpty()) {
            BBNode u = pq.poll();

            for (Vertex v : adjacencyList.get(u.vertex.name).neighbours.keySet()) {
                if (v.type == VertexType.DEPOT          // never go from depot to depot
                        && u.vertex.type == VertexType.DEPOT) continue;

                if (v.type == VertexType.DEPOT){        // if you going to depot just go
                    addNodeToPriorityQueue(new BBNode(v, u.timeElapsed, u.penaltyTaken, u));
                }

                if (v.type == VertexType.CUSTOMER){
                    if (u.remainedGoods < v.demand) continue;
                    if (u.servicedNodes[v.customerId] == true) continue;

                    if (u.timeElapsed + getDistance(u.vertex, v) > v.latestTime)
                        addedPenalty = v.penalty * (u.timeElapsed + getDistance(u.vertex, v) - v.latestTime);
                    else if (u.timeElapsed + getDistance(u.vertex, v) < v.earliestTime)
                        addedTime = getDistance(u.vertex, v) + (v.earliestTime - u.timeElapsed + getDistance(u.vertex, v));
                    else
                        addedTime = getDistance(u.vertex, v);

                    BBNode newNode = new BBNode(v, u.timeElapsed + addedTime, u.penaltyTaken + addedPenalty, u);

                    addNodeToPriorityQueue(newNode);
                }
            }
        }
    }

    /**
     * add new node to the queue and check some criteria
     * @param newNode node that must be added to the pq.
     */
    void addNodeToPriorityQueue(BBNode newNode){
        // if this node is an answer
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers
                && newNode.cost() < minimumCost) {
            bestNode = newNode;
            minimumCost = newNode.cost();
            return;
        }
        // if this node is a terminal node and not reducing the minimum answer throw it out.
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers) {
            return;
        }

        // if this node is a intermediate node add it to the queue.
        pq.add(newNode);
    }

    /**
     * print the answer
     */
    public void printTheAnswer(){
        String[] paths = bestNode.getStringPath().split("\n");

        for (String path : paths)
            System.out.println("Depot " + path + " -> Depot");

        System.out.println("Minimum Cost: " + minimumCost);
    }
}
