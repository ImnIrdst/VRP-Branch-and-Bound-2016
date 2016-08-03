package VRP.Algorithms.BranchAndBound;

import VRP.Algorithms.Dijkstra.Dijkstra;
import VRP.GlobalVars;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * a branch and bound algorithm for
 * solving vehicle routing problem (VRP)
 */
public class BranchAndBound {
    private Graph graph;

    public double minimumCost;                // minimum cost we found
    public BBNode bestNode;                   // best node we found

    private PriorityQueue<BBNode> pq;         // use priority queue (min heap) for best first search

    /**
     * constructor for a given graph
     *
     * @param graph a graph that has a Map<String, Vertex> adjacencyList
     */
    public BranchAndBound(Graph graph, double minimumCost) {
        this.graph = graph;
        this.minimumCost = minimumCost;
        GlobalVars.minimumValue = this.minimumCost;
        // fill the Global variables
        GlobalVars.bbGraph = graph;

        this.pq = new PriorityQueue<>(10, new Comparator<BBNode>() {
            @Override
            public int compare(BBNode u, BBNode v) {
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
        System.out.println("--------------------------");
        System.out.println("Branch and bound algorithm");
        System.out.println("--------------------------");
        // add initial node
        Vertex depotVertex = graph.getVertexByName(depotName);
        pq.add(new BBNode(depotVertex, null));

        // go down the tree
        while (!pq.isEmpty()) {
            BBNode u = pq.poll();
            if (u.vertex.getId() == 1
                    && u.parent != null && u.parent.vertex.getId() == 4
                    && u.parent.parent != null && u.parent.parent.vertex.getId() == 2
                    && u.parent.parent.parent != null && u.parent.parent.parent.vertex.getId() == 3)
                u = u;

            if (u.vertex.getId() == 4
                    && u.parent != null && u.parent.vertex.getId() == 2
                    && u.parent.parent != null && u.parent.parent.vertex.getId() == 3
                    ) // && u.parent.parent.parent != null && u.parent.parent.parent.vertex.getId() == 3)
                u = u;

            if (u.vertex.getId() == 2
                    && u.parent != null && u.parent.vertex.getId() == 3
                    )//&& u.parent.parent != null && u.parent.parent.vertex.getId() == 3
                    //&& u.parent.parent.parent != null && u.parent.parent.parent.vertex.getId() == 3)
                u = u;
            if (canBePruned(u)) continue;

            for (Vertex v : u.vertex.neighbours.keySet()) {
                if (v.type == VertexType.DEPOT          // never go from depot to depot
                        && u.vertex.type == VertexType.DEPOT) continue;

                if (v.type == VertexType.DEPOT) {        // if you going to depot just go
                    addNodeToPriorityQueue(new BBNode(v, u)); continue;
                }

                if (u.vertex.type == VertexType.DEPOT
                        && v.type == VertexType.CUSTOMER
                        && v.hasVehicle == 0) continue;

                if (v.type == VertexType.CUSTOMER) {
                    // pruning criteria
                    // if (u.remainedCapacity < v.demand) continue;   // check demand criterion
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
                && newNode.getCost() <= minimumCost) {
            bestNode = newNode;
            minimumCost = newNode.getCost();
            GlobalVars.minimumValue = minimumCost;
            return;
        }

        // if this node is a intermediate node add it to the queue.
        if (!canBePruned(newNode)) {
            pq.add(newNode);
            GlobalVars.numberOfBranchAndBoundNodes++;
        }
    }

    /**
     * @param newNode: node that must be checked
     * @return true of node can be pruned from the tree
     */
    boolean canBePruned(BBNode newNode) {
        // if new node capacity is negative
        if (newNode.remainedCapacity < 0)
            return true;

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
        System.out.println("Format -> VertexName (arrivalTime, thisVertexPenalty, dueDate)");
        System.out.println(bestNode.getStringPath() + "\n");
        System.out.println(bestNode.getPrintCostDetailsString());
    }
}
