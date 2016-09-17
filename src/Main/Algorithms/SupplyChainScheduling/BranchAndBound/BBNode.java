package Main.Algorithms.SupplyChainScheduling.BranchAndBound;

import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Edge;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * a node that used in branch and bound for VRP
 */
public class BBNode {
    public Vertex vertex;          // current vertex of the graph
    public BBNode parent;          // parent of the node in the BBAutoTest tree

    public List<Integer> waitingList; // customers that must be served
    public double cumulativeProcessTime; // process time of served customers

    public int vehicleUsed;        // number of vehicle used in this node
    public double vehicleUsageCost;
    public double curTimeElapsed;     // the time elapsed after moving the vehicle in current path
    public double maxTimeElapsed;     // the maximum time elapsed in all paths
    public double cumulativePenaltyTaken; // cumulative penalty taken in all nodes
    public double cumulativeTimeTaken; // cumulative time that all vehicles spend to serve the customers
    public int remainedCapacity;   // remained goods in the car
    public boolean[] servicedNodes; // nodes that are serviced
    public int numberOfServicedCustomers; // for easily terminate the algorithm
    public double startTime;          // it's the time when vehicle starts moving
    public double arrivalTime;        // the moment that the vehicle reached to the node
    public double thisVertexPenalty;  // the penalty that taken in this vertex


    public SimpleTSP tsp;

    public double lowerBoundForVehicleCost;
    public double lowerBoundForPenaltyTaken;
    public double lowerBoundForTravelTime;
    public double lowerBoundForDeeperLevels;

    /**
     * Default Constructor (Empty Constructor)
     */
    public BBNode() {
    }

    /**
     * constructor for the branch and bound node
     */
    public BBNode(Vertex vertex, BBNode parent) {
        this.vertex = vertex;
        this.parent = parent;

        this.calculateWaitingList();
        this.calculateCumulativeProcessTime();
        this.calculateTsp();
        this.calculateVehicleUsed();
        this.calculateVehicleUsageCost();
        this.calculateCurTimeElapsed();
        this.calculateRemainedCapacity();
        this.calculateCumulativePenaltyTaken();
        this.calculateCumulativeTimeTaken();
        this.calculateServicedNodes();

        this.calculateLowerBoundForPenaltyTaken();
        this.calculateLowerBoundForMinimumVehicleUsageCost();
        this.calculateLowerBoundForTravelTime();


    }


    /**
     * calculateWaitingList
     */
    public void calculateWaitingList() {
        this.waitingList = new ArrayList<>();

        if (parent != null && parent.vertex.type == VertexType.DEPOT) {
            this.waitingList.add(vertex.getId());

        }
        if (parent != null && parent.vertex.type != VertexType.DEPOT) {
            for (int i = 0; i < parent.waitingList.size(); i++) {
                this.waitingList.add(parent.waitingList.get(i));
            }
            this.waitingList.add(vertex.getId());
        }
    }

    public void calculateCumulativeProcessTime() {
        if (parent == null)
            cumulativeProcessTime = 0;
        else if (this.vertex.type == VertexType.CUSTOMER)
            cumulativeProcessTime += parent.cumulativeProcessTime + this.vertex.processTime;
        else
            cumulativeProcessTime += parent.cumulativeProcessTime;
    }

    /**
     * calculateTsp
     */
    private void calculateTsp() {
        if (parent != null && this.vertex.type == VertexType.DEPOT) {
            this.tsp = new SimpleTSP(GlobalVars.ppGraph, this.waitingList, this.cumulativeProcessTime);
            this.tsp.run();
        }
    }


    /**
     * calculateVehicleUsed
     */
    public void calculateVehicleUsed() {
        if (parent == null)
            this.vehicleUsed = 0;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.vehicleUsed = parent.vehicleUsed + 1;
        else
            this.vehicleUsed = parent.vehicleUsed;
    }

    /**
     * calculateVehicleUsageCost
     */
    public void calculateVehicleUsageCost() {
        if (parent == null)
            this.vehicleUsageCost = 0;
        else if (this.vertex.type == VertexType.DEPOT)
            this.vehicleUsageCost = parent.vehicleUsageCost + this.vertex.fixedCost;
        else
            this.vehicleUsageCost = parent.vehicleUsageCost;
    }

    /**
     * calculateCurTimeElapsed
     */
    public void calculateCurTimeElapsed() {
        if (parent == null) curTimeElapsed = 0;

        else if (this.vertex.type == VertexType.DEPOT)
            this.curTimeElapsed = tsp.arrivalTime;
    }

    /**
     * calculateRemainedCapacity
     */
    public void calculateRemainedCapacity() {
        if (parent == null)
            this.remainedCapacity = 0;

        else if (this.vertex.type == VertexType.CUSTOMER
                && parent.vertex.type == VertexType.DEPOT)
            this.remainedCapacity = parent.vertex.capacity - 1;

        else if (this.vertex.type == VertexType.CUSTOMER)
            this.remainedCapacity = parent.remainedCapacity - 1;

        else
            this.remainedCapacity = parent.remainedCapacity;
    }

    /**
     * calculateCumulativePenaltyTaken
     */
    public void calculateCumulativePenaltyTaken() {
        if (parent == null)
            this.cumulativePenaltyTaken = 0;

        else if (this.vertex.type == VertexType.DEPOT)
            this.cumulativePenaltyTaken = parent.cumulativePenaltyTaken + tsp.penaltyTaken;

        else
            this.cumulativePenaltyTaken = parent.cumulativePenaltyTaken;
    }

    /**
     * calculateCumulativeTimeTaken
     */
    public void calculateCumulativeTimeTaken() {
        if (parent == null)
            this.cumulativeTimeTaken = 0;

        else if (this.vertex.type == VertexType.DEPOT)
            this.cumulativeTimeTaken = parent.cumulativeTimeTaken + tsp.travelTime;

        else
            this.cumulativeTimeTaken = parent.cumulativeTimeTaken;
    }

    /**
     * calculateServicedNodes
     */
    public void calculateServicedNodes() {
        if (parent == null) {
            this.numberOfServicedCustomers = 0;
            this.servicedNodes = new boolean[GlobalVars.numberOfCustomers];

        } else {
            this.servicedNodes = Arrays.copyOf(parent.servicedNodes, parent.servicedNodes.length);
            this.numberOfServicedCustomers = parent.numberOfServicedCustomers;

            if (this.vertex.type == VertexType.CUSTOMER) {
                this.numberOfServicedCustomers++;
                this.servicedNodes[this.vertex.id] = true;
            }
        }
    }

    /**
     * calculates Lower Bound For Minimum Vehicle Usage Cost
     */
    public void calculateLowerBoundForMinimumVehicleUsageCost() {
        int remainedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
        int vehiclesNeeded = (remainedCustomers - this.remainedCapacity) / GlobalVars.depot.capacity;

        lowerBoundForVehicleCost = vehiclesNeeded * GlobalVars.depot.fixedCost;
    }

    /**
     * calculates a lower bound for cumulative time needed for all the vehicles to serve all customers
     */
    public void calculateLowerBoundForTravelTime() {

        double lowerBound = 0;

        // for each extra needed vehicle peek an edge from depot an mark the end nodes
        int extraVehiclesNeeded = getMinimumNumberOfExtraVehiclesNeeded();
        Vertex depotNode = GlobalVars.ppGraph.getVertexById(GlobalVars.depotId);

        List<Edge> depotEdges = new ArrayList<>();
        for (Vertex v : depotNode.neighbours.keySet()) {
            if (this.servicedNodes[v.getId()] == false)
                depotEdges.add(new Edge(depotNode, v, depotNode.neighbours.get(v)));
        }
        Collections.sort(depotEdges);

        for (int i = 1; i < Math.min(extraVehiclesNeeded, depotEdges.size()); i++) {
            lowerBound += depotEdges.get(i).weight;
        }

        // for other nodes peek the minimum edges
        for (Vertex v : GlobalVars.ppGraph.getCustomerVertices()) {
            if (this.servicedNodes[v.getId()] == false)
                lowerBound += getSecondMinimumEdgeWeightOfVertex(v);
        }

        this.lowerBoundForTravelTime = lowerBound;

    }

    /**
     * If go from this vertex to the depot, when I arrive
     * there and how much penalty I must take.
     * <p/>
     * calculates the lower bound for additional penalty taken
     */
    public void calculateLowerBoundForPenaltyTaken() {
        if (parent == null) {
            lowerBoundForPenaltyTaken = 0;
            for (Vertex v : GlobalVars.ppGraph.getVertices()) {
                double minimumArrivalTime = getMinimumEdgeWeightOfVertex(v) + v.processTime;
                lowerBoundForPenaltyTaken += Math.max(0, minimumArrivalTime - v.dueDate) * v.penalty;
            }
        } else
            lowerBoundForPenaltyTaken = parent.lowerBoundForPenaltyTaken;

        if (vertex.type == VertexType.CUSTOMER) {
            double minimumArrivalTime = getMinimumEdgeWeightOfVertex(vertex) + vertex.processTime;
            lowerBoundForPenaltyTaken -= Math.max(0, minimumArrivalTime - vertex.dueDate) * vertex.penalty;
        }
    }

    /**
     * First Consider only level 1
     */
    public void calculateLowerboundForDeeperLevels() {
        List<BBNode> childs = new ArrayList<>();
        for (Vertex v : vertex.neighbours.keySet()) {
            if (this.servicedNodes[v.id] == true) continue;

            BBNode child = new BBNode();
        }
    }


    /**
     * @return cost of the node that we are there
     */
    public double getCost() {            // calculates branch and bound cost of the node
        return cumulativeTimeTaken + cumulativePenaltyTaken + vehicleUsageCost;
    }

    /**
     * Calculates lower bound for this node
     *
     * @return lower bound for this node
     */
    public double getLowerBound() {
//        return 0;
        return lowerBoundForVehicleCost + lowerBoundForTravelTime + lowerBoundForPenaltyTaken + lowerBoundForDeeperLevels;
    }

    // --------------   helper functions ---------------

    /**
     * @return minimum number of extra vehicles needed to serve the remaining customers
     */
    public int getMinimumNumberOfExtraVehiclesNeeded() {
        int remainedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
        return (remainedCustomers - this.remainedCapacity) / GlobalVars.depot.capacity;
    }

    /**
     * @return minimum edge weight of a given vertex
     */
    public double getSecondMinimumEdgeWeightOfVertex(Vertex v) {
        double min = Integer.MAX_VALUE;

        for (Vertex u : v.neighbours.keySet()) {
            if (u.id == v.id) continue;
            if (u.id != this.vertex.id
                    && u.type == VertexType.CUSTOMER
                    && this.servicedNodes[u.id] == true) continue;

            if (min > GlobalVars.ppGraph.getDistance(u, v)) {
                min = GlobalVars.ppGraph.getDistance(u, v);
            }
        }
        return min;
    }

    /**
     * @return minimum edge weight of a given vertex
     */
    public double getMinimumEdgeWeightOfVertex(Vertex v) {
        double min = Integer.MAX_VALUE;

        int minId = -1;
        for (Vertex u : v.neighbours.keySet()) {
            if (u.id == v.id) continue;
            if (u.id != this.vertex.id
                    && u.type == VertexType.CUSTOMER
                    && this.servicedNodes[u.id] == true) continue;

            if (min > GlobalVars.ppGraph.getDistance(u, v)) {
                min = GlobalVars.ppGraph.getDistance(u, v);
                minId = u.getId();
            }
        }

        min = Integer.MAX_VALUE;
        for (Vertex u : v.neighbours.keySet()) {
            if (u.id == v.id) continue;
            if (u.id != this.vertex.id
                    && u.type == VertexType.CUSTOMER
                    && this.servicedNodes[u.id] == true) continue;

            if (min >= GlobalVars.ppGraph.getDistance(u, v) && u.getId() != minId) {
                min = GlobalVars.ppGraph.getDistance(u, v);
            }
        }

        return min;
    }

    /**
     * @return minimum time needed to end this path
     */
    public double getMinimumAdditionalTimeNeededToTheEndThePath() {
        Vertex depotVertex = GlobalVars.ppGraph.getVertexByName(GlobalVars.depotName);

        if (this.vertex.type != VertexType.DEPOT)
            return this.vertex.neighbours.get(depotVertex);
        return 0;
    }


    // --------------   result printing functions ---------------

    /**
     * go up int the tree and print the path
     */
    public String getStringPath() {
        StringBuilder sb = new StringBuilder("");
        for (BBNode node = this; node != null; node = node.parent) {
            if (node.vertex.type == VertexType.DEPOT && node.parent != null) {
                sb.append(node.tsp.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * @return Detail of attributes that affects the cost
     */
    public String getPrintCostDetailsString() {
        return "Time needed: " + String.format("%.2f", maxTimeElapsed) + "\n"
                + "Travel Time of all vehicles: " + String.format("%.2f", cumulativeTimeTaken) + "\n"
                + "Penalty Taken of all vehicles: " + String.format("%.2f", cumulativePenaltyTaken) + "\n"
                + "Number of Vehicles Used: " + vehicleUsed + "\n"
                + "Cumulative Vehicles Usage Cost: " + vehicleUsageCost + "\n"
                + "Minimum Cost for the problem: " + String.format("%.2f", getCost());
    }

    /**
     * details of the node stat for the to string function
     */
    public String detailsForToString() {
        return String.format(", %.2f, %.2f, %d",
                thisVertexPenalty, vertex.dueDate, vertex.penalty);
    }

    @Override
    public String toString() {
        return vertex.toString() + String.format("(%.2f)", getCost());
    }
}

//-------------- Trash -------------
//        ArrayList<Integer> edgeWeightsFromDepotToUnservicedCustomers = new ArrayList<>();
//
//        Vertex depotVertex = Main.GlobalVars.ppGraph.adjacencyList.get(Main.GlobalVars.depotName);
//        for (Vertex u : Main.GlobalVars.ppGraph.adjacencyList.values()) {
//            if (u.type == VertexType.CUSTOMER && this.servicedNodes[u.id] == false) {
//                edgeWeightsFromDepotToUnservicedCustomers.add(depotVertex.neighbours.get(u));
//                edgeWeightsFromDepotToUnservicedCustomers.add(u.neighbours.get(depotVertex));
//            }
//        }
//        if (this.vertex.type == VertexType.CUSTOMER) {
//            edgeWeightsFromDepotToUnservicedCustomers.add(this.vertex.neighbours.get(depotVertex));
//        }
//        Collections.sort(edgeWeightsFromDepotToUnservicedCustomers);
//
//        int vehiclesNeeded = getMinimumNumberOfExtraVehiclesNeeded();
//        if (vehiclesNeeded * 2 > edgeWeightsFromDepotToUnservicedCustomers.size()) {
//            System.out.println("There Is a Bug in BBNode.getMinimumAdditionalTimeNeededToTheEndThePath()!!!!!!!!!!!!!!!!!!!!!!!!");
//            return Integer.MAX_VALUE / 2;
//        }
//
//        if (vehiclesNeeded == 0) return 0;
//
//        return edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 1)
//                + edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 2);