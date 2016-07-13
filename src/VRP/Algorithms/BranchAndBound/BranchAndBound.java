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

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BranchAndBound(Graph graph) {
        this.adjacencyList = graph.adjacencyList;
        this.minimumCost = Integer.MAX_VALUE;
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
        // use priority queue for best first search
        PriorityQueue<BBNode> pq = new PriorityQueue<>(10, new Comparator<BBNode>() {
            @Override
            public int compare(BBNode u, BBNode v) {
                return Integer.compare(u.cost(), v.cost());
            }
        });

        // initial node
        pq.add(new BBNode(adjacencyList.get(depotName), 0, 0, null));

        // go down the tree
        int addedTime = 0;
        int addedPenalty = 0;
        while (!pq.isEmpty()) {
            BBNode u = pq.poll();

            if (u.servicedNodes[0] && u.servicedNodes[1])
                u = u;
            if (u.servicedNodes[0] && u.servicedNodes[1] && u.servicedNodes[2])
                u = u;
            if (u.servicedNodes[0] && u.servicedNodes[1] && u.servicedNodes[2] && u.servicedNodes[3])
                u = u;

            for (Vertex v : adjacencyList.get(u.vertex.name).neighbours.keySet()) {
                if (v.type == VertexType.DEPOT          // never go from depot to depot
                        && u.vertex.type == VertexType.DEPOT) continue;

                if (v.type == VertexType.DEPOT){        // if you going to depot just go
                    BBNode newNode = new BBNode(v, u.timeElapsed, u.penaltyTaken, u);

                    // if this node is an answer
                    if (newNode.vertex.type == VertexType.DEPOT
                            && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers
                            && newNode.cost() < minimumCost) {
                        bestNode = newNode;
                        minimumCost = newNode.cost();
                        continue;
                    }
                    // if this node is a terminal node and not reducing the minimum answer throw it out.
                    if (newNode.vertex.type == VertexType.DEPOT
                            && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers) {
                        continue;
                    }

                    pq.add(newNode); continue;
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

                    // if this node is an answer
                    if (newNode.vertex.type == VertexType.DEPOT
                            && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers
                            && newNode.cost() < minimumCost) {
                        bestNode = newNode;
                        minimumCost = newNode.cost();
                        continue;
                    }
                    // if this node is a terminal node and not reducing the minimum answer throw it out.
                    if (newNode.vertex.type == VertexType.DEPOT
                            && newNode.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers) {
                        continue;
                    }

                    // if this node is a intermediate node add it to the queue.
                    pq.add(newNode);
                }
            }
        }
    }

    /**
     * print the answer
     */
    public void printTheAnswer(){
        bestNode.printPath();
        System.out.println("\n\nMinimum Cost: " + minimumCost);
    }
}
