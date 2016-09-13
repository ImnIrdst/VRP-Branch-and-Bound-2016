package Main.Algorithms.SupplyChainScheduling.BranchAndBound;

import Main.Algorithms.Dijkstra.Dijkstra;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;
import Main.GlobalVars;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Objects;
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
    public BranchAndBound(Graph graph, double upperBound) {
        this.graph = graph;
        this.minimumCost = upperBound;

        // fill the Global variables
        GlobalVars.ppGraph = graph;
        GlobalVars.minimumValue = this.minimumCost;

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
//        System.out.println("--------------------------");
//        System.out.println("Branch and bound algorithm");
//        System.out.println("--------------------------");

        // add initial node
        Vertex depotVertex = graph.getVertexByName(depotName);
        pq.add(new BBNode(depotVertex, null));

        // go down the tree
        while (!pq.isEmpty()) {
            BBNode u = pq.poll();
            if (canBePruned(u)) continue;

            for (Vertex v : u.vertex.neighbours.keySet()) {
                if (v.type == VertexType.DEPOT && u.waitingList.size() == 0) continue;
                if (v.type != VertexType.DEPOT && u.servicedNodes[v.id] == true) continue;

                BBNode newNode = new BBNode(v, u);
                addNodeToPriorityQueue(newNode);
            }
        }
    }

    /**
     * add new node to the queue and check some criteria
     *
     * @param newNode node that must be added to the pq.
     */
    void addNodeToPriorityQueue(BBNode newNode) {
//        if (getHierarchy(newNode).equals("764 -> 771 -> 556 -> 764 -> 453 -> 652 -> 10 -> 493 -> 764"))
//            System.out.println(newNode.getStringPath() + "\n" + newNode.getPrintCostDetailsString());

        if (canBePruned(newNode)) return;


//        System.out.println(getHierarchy(newNode));
        // if this node is an answer
        if (newNode.vertex.type == VertexType.DEPOT
                && newNode.numberOfServicedCustomers == graph.getCustomersQty()
                && newNode.getCost() <= minimumCost) {
//            System.out.println(getHierarchy(newNode));
            bestNode = newNode;
            minimumCost = newNode.getCost();
            GlobalVars.minimumValue = minimumCost;
            return;
        }

        pq.add(newNode);
        GlobalVars.numberOfBranchAndBoundNodes++;
    }

    /**
     * @param newNode: node that must be checked
     * @return true of node can be pruned from the tree
     */
    boolean canBePruned(BBNode newNode) {
        if (newNode.vehicleUsed > GlobalVars.numberOfVehicles)
            return true; // TODO: Check This
        // if new node capacity is negative
        if (newNode.remainedCapacity < 0)
            return true;

        // if new Node so far cost is more than minimum cost
        if (newNode.getCost() > minimumCost)
            return true;

        // check lower bound
        if (newNode.getCost() + newNode.getLowerBound() > minimumCost)
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

    public String getHierarchy(BBNode bbNode) {
        String string = bbNode.vertex.name; // String.format("%s(%.2f)", bbNode.vertex.name, bbNode.getCost());
        for (BBNode node = bbNode.parent ; node != null ; node = node.parent){
            string = node.vertex.name + " -> " + string;
        }

        return string;
    }
}
