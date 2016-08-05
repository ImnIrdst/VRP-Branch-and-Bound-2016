package VRP.Algorithms.BranchAndBound;

import VRP.Algorithms.Other.CapacityCostPair;
import VRP.Algorithms.Other.Greedy;
import VRP.GlobalVars;
import VRP.Graph.Edge;
import VRP.Graph.Graph;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * a node that used in branch and bound for VRP
 */
public class BBNode {
    public Vertex vertex;          // current vertex of the graph
    public int vehicleUsed;        // number of vehicle used in this node
    public double vehicleUsageCost;
    public double curTimeElapsed;     // the time elapsed after moving the vehicle in current path
    public double maxTimeElapsed;     // the maximum time elapsed in all paths
    public int remainedCapacity;   // remained goods in the car
    public double cumulativePenaltyTaken; // cumulative penalty taken in all nodes
    public double cumulativeTimeTaken; // cumulative time that all vehicles spend to serve the customers
    public boolean[] servicedNodes; // nodes that are serviced
    public int numberOfServicedCustomers; // for easily terminate the algorithm
    public BBNode parent;          // parent of the node in the BB tree

    public double startTime;          // it's the time when vehicle starts moving
    public double arrivalTime;        // the moment that the vehicle reached to the node
    public double thisVertexPenalty;  // the penalty that taken in this vertex

    public double lowerBoundForVehicleCost;
    public double lowerBoundForPenaltyTaken;
    public double lowerBoundForTimeTaken;

    /**
     * constructor for the branch and bound node
     */
    public BBNode(Vertex vertex, BBNode parent) {
        this.vertex = vertex;
        this.parent = parent;
        this.calculateVehicleUsed();
        this.calculateVehicleUsageCost();
        this.calculateCurTimeElapsed();
        this.calculateMaxTimeElapsed();
        this.calculateRemainedCapacity();
        this.calculateArrivalTime();
        this.calculateThisVertexPenalty();
        this.calculateCumulativePenaltyTaken();
        this.calculateCumulativeTimeTaken();
        this.calculateServicedNodes();
        this.calculateParentStartTime();

        this.calculateLowerBoundForPenaltyTaken();
        this.calculateLowerBoundForMinimumVehicleUsageCost();
        this.calculateLowerBoundForCumulativeTimeNeededForAllVehicles();

        long elapsedTime = System.currentTimeMillis() - GlobalVars.startTime;

        if (elapsedTime > GlobalVars.bbPrintTime) {
            GlobalVars.bbPrintTime += GlobalVars.printTimeStepSize;
            System.out.printf("Time: %.1fs,\t\t", GlobalVars.bbPrintTime / 1000.);
            System.out.printf("Minimum value: %.2f,\t\t", GlobalVars.minimumValue);
            System.out.print("Nodes: " + GlobalVars.numberOfBranchAndBoundNodes + "\n");
        }

    }

    /**
     * calculateVehicleUsed
     */
    public void calculateVehicleUsed() {
        if (parent == null)
            vehicleUsed = 0;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.vehicleUsed = parent.vehicleUsed + 1;
        else
            this.vehicleUsed = parent.vehicleUsed;
    }

    public void calculateVehicleUsageCost() {
        if (parent == null)
            vehicleUsageCost = 0;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.vehicleUsageCost = parent.vehicleUsageCost + this.vertex.fixedCost;
        else
            this.vehicleUsageCost = parent.vehicleUsageCost;
    }

    /**
     * calculateCurTimeElapsed
     */
    public void calculateCurTimeElapsed() {
        if (parent == null) curTimeElapsed = 0;

        else if (parent.vertex.type == VertexType.DEPOT)
            this.curTimeElapsed = this.vertex.mdt;

        else if (parent.vertex.type == VertexType.CUSTOMER)
            this.curTimeElapsed = parent.curTimeElapsed + GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);

        calculateMaxTimeElapsed();
        if (this.vertex.type == VertexType.DEPOT) this.curTimeElapsed = 0;
    }

    /**
     * calculateMaxTimeElapsed
     */
    public void calculateMaxTimeElapsed() {
        if (parent == null) {
            this.maxTimeElapsed = 0;
            return;
        }
        this.maxTimeElapsed = Math.max(this.maxTimeElapsed, parent.maxTimeElapsed);
        this.maxTimeElapsed = Math.max(this.maxTimeElapsed, this.curTimeElapsed);
    }

    /**
     * calculateRemainedCapacity
     */
    public void calculateRemainedCapacity() {
        if (parent == null)
            this.remainedCapacity = 0;
        else if (this.vertex.type == VertexType.CUSTOMER
                && this.parent.vertex.type == VertexType.DEPOT)
            this.remainedCapacity = this.vertex.capacity - this.vertex.demand;
        else if (this.vertex.type == VertexType.CUSTOMER)
            this.remainedCapacity = parent.remainedCapacity - this.vertex.demand;
        else
            this.remainedCapacity = parent.remainedCapacity;
    }

    /**
     * calculateArrivalTime
     */
    public void calculateArrivalTime() {
        if (parent == null)
            this.arrivalTime = -1;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.arrivalTime = this.vertex.mdt;
        else
            this.arrivalTime = parent.arrivalTime + GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);
    }

    /**
     * calculateThisVertexPenalty
     */
    public void calculateThisVertexPenalty() {
        if (this.arrivalTime > this.vertex.dueDate)
            this.thisVertexPenalty = this.vertex.penalty * (this.arrivalTime - this.vertex.dueDate);
        else
            this.thisVertexPenalty = 0;
    }

    /**
     * calculateCumulativePenaltyTaken
     */
    public void calculateCumulativePenaltyTaken() {
        if (parent == null)
            this.cumulativePenaltyTaken = 0;
        else
            this.cumulativePenaltyTaken = parent.cumulativePenaltyTaken + this.thisVertexPenalty;
    }

    /**
     * calculateCumulativeTimeTaken
     */
    public void calculateCumulativeTimeTaken() {
        if (parent == null)
            this.cumulativeTimeTaken = 0;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.cumulativeTimeTaken = parent.cumulativeTimeTaken;
        else
            this.cumulativeTimeTaken = parent.cumulativeTimeTaken + GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);
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
                this.servicedNodes[this.vertex.customerId] = true;
            }
        }

    }

    /**
     * calculateParentStartTime
     */
    public void calculateParentStartTime() {
        if (parent != null && parent.vertex.type == VertexType.DEPOT)
            parent.startTime = 0; // this.arrivalTime - GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);

        // for finished nodes.
        if (parent != null && this.vertex.type == VertexType.DEPOT
                && this.numberOfServicedCustomers == GlobalVars.numberOfCustomers)
            this.startTime = -1;
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
        return lowerBoundForVehicleCost + lowerBoundForTimeTaken + lowerBoundForPenaltyTaken;
    }

    /**
     * calculates Lower Bound For Minimum Vehicle Usage Cost
     */
    public void calculateLowerBoundForMinimumVehicleUsageCost() {
        int numberOfUnservicedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;

        Integer[] unservicedCustomersDemands = new Integer[numberOfUnservicedCustomers];
        List<CapacityCostPair> vehicleCapacities = new ArrayList<>();

        int sumOfDemands = 0;
        int sumOfCapacity = 0;
        for (int i = 0, j = 0; i < GlobalVars.numberOfCustomers; i++) {
            if (this.servicedNodes[i] == false) {
                Vertex v = GlobalVars.bbGraph.getVertexById(i);
                unservicedCustomersDemands[j++] = v.demand;
                sumOfDemands += v.demand;

                if (v.hasVehicle == 1) {
                    vehicleCapacities.add(new CapacityCostPair(v.capacity, v.fixedCost));
                    sumOfCapacity += v.capacity;
                }
            }
        }

        if (sumOfCapacity + remainedCapacity < sumOfDemands)
            this.lowerBoundForVehicleCost = GlobalVars.INF;
        else
            this.lowerBoundForVehicleCost = Greedy.minimumExtraVehicleUsageCostNeeded(
                    unservicedCustomersDemands, remainedCapacity, vehicleCapacities);
    }

    /**
     * calculates a lower bound for cumulative time needed for all the vehicles to serve all customers
     */
    public void calculateLowerBoundForCumulativeTimeNeededForAllVehicles() {
        double lowerBound = 0;
        boolean[] markedNodes = new boolean[GlobalVars.numberOfCustomers];

        // for each extra needed vehicle peek an edge from depot an mark the end nodes
        int extraVehiclesNeeded = getLowerBoundForNumberOfExtraVehiclesNeeded();
        Vertex depotNode = GlobalVars.bbGraph.getVertexById(GlobalVars.depotId);

        List<Edge> depotEdges = new ArrayList<>();
        for (Vertex v : depotNode.neighbours.keySet()) {
            if (this.servicedNodes[v.getId()] == false)
                depotEdges.add(new Edge(depotNode, v, depotNode.neighbours.get(v)));
        }
        Collections.sort(depotEdges);

        for (int i = 0; i < Math.min(extraVehiclesNeeded, depotEdges.size()); i++) {
            lowerBound += depotEdges.get(i).weight;
            markedNodes[depotEdges.get(i).v.getId()] = true;
        }

        // for other nodes peek the minimum edges
        for (Vertex v : GlobalVars.bbGraph.getCustomerVertices()) {
            if (markedNodes[v.getId()] == false && this.servicedNodes[v.getId()] == false)
                lowerBound += getMinimumEdgeWeightOfVertex(v);
        }

        this.lowerBoundForTimeTaken = lowerBound;
    }

    /**
     * If go from this vertex to the depot, when I arrive
     * there and how much penalty I must take.
     * <p/>
     * calculates the lower bound for additional penalty taken
     */
    public void calculateLowerBoundForPenaltyTaken() {
        if (this.vertex.type == VertexType.DEPOT) return;

        Vertex depotVertex = GlobalVars.bbGraph.getVertexByName(GlobalVars.depotName);
        double lowestFinishTime = this.curTimeElapsed + this.getMinimumAdditionalTimeNeededToTheEndThePath();

        if (lowestFinishTime > depotVertex.dueDate)
            this.lowerBoundForPenaltyTaken = (lowestFinishTime - depotVertex.dueDate) * depotVertex.penalty;
        else
            this.lowerBoundForPenaltyTaken = 0;
    }

    /**
     * @return minimum number of extra vehicles needed to serve the remaining customers
     */
    public int getLowerBoundForNumberOfExtraVehiclesNeeded() {
        int numberOfUnservicedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
        Integer[] unservicedCustomersDemands = new Integer[numberOfUnservicedCustomers];

        int sumOfDemands = 0;
        int sumOfCapacity = 0;
        int maximumCapacity = 0;
        for (int i = 0, j = 0; i < GlobalVars.numberOfCustomers; i++) {
            if (this.servicedNodes[i] == false) {
                Vertex v = GlobalVars.bbGraph.getVertexById(i);
                unservicedCustomersDemands[j++] = v.demand;
                sumOfDemands += v.demand;

                if (v.hasVehicle == 1) {
                    sumOfCapacity += v.capacity;
                    maximumCapacity = Math.max(v.capacity, maximumCapacity);
                }
            }
        }

        if (sumOfCapacity + remainedCapacity < sumOfDemands)
            return (int) GlobalVars.INF;

        int extraVehiclesNeeded = Greedy.minimumExtraVehiclesNeeded(
                unservicedCustomersDemands, this.remainedCapacity, maximumCapacity
        );

        return extraVehiclesNeeded;
    }

    /**
     * @return minimum edge weight of a given vertex
     */
    public double getMinimumEdgeWeightOfVertex(Vertex v) {
        double min = Integer.MAX_VALUE;
        for (Vertex u : v.neighbours.keySet()) {
            if (u.name.equals(v.name)) continue;
            if (!u.name.equals(this.vertex.name)
                    && u.type == VertexType.CUSTOMER
                    && this.servicedNodes[u.customerId] == true) continue;

            min = Math.min(min, GlobalVars.bbGraph.getDistance(u, v));
        }
        return min;
    }

    /**
     * @return minimum time needed to end this path
     */
    public double getMinimumAdditionalTimeNeededToTheEndThePath() {
        Vertex depotVertex = GlobalVars.bbGraph.getVertexByName(GlobalVars.depotName);

        if (this.vertex.type != VertexType.DEPOT)
            return this.vertex.neighbours.get(depotVertex);
        return 0;
    }

    /**
     * go up int the tree and print the path
     */
    public String getStringPath() {
        if (this.parent == null) {
            return this.toString(); //System.out.print(this.vertex);
        } else {
            return parent.getStringPath() + " -> " + this;
        }
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
        return ", " + thisVertexPenalty + ", " + vertex.dueDate;
    }

    @Override
    public String toString() {
        if (vertex.type == VertexType.DEPOT && startTime == -1)
            return vertex + " (" + arrivalTime + detailsForToString() + ")";

        else if (vertex.type == VertexType.DEPOT && arrivalTime == -1)
            return "";

        else if (vertex.type == VertexType.DEPOT)
            return vertex + " (" + arrivalTime + detailsForToString() + ")" + "\n";

        return vertex + " (" + arrivalTime + detailsForToString() + ")";
    }

}

//-------------- Trash -------------
//        ArrayList<Integer> edgeWeightsFromDepotToUnservicedCustomers = new ArrayList<>();
//
//        Vertex depotVertex = GlobalVars.bbGraph.adjacencyList.get(GlobalVars.depotName);
//        for (Vertex u : GlobalVars.bbGraph.adjacencyList.values()) {
//            if (u.type == VertexType.CUSTOMER && this.servicedNodes[u.customerId] == false) {
//                edgeWeightsFromDepotToUnservicedCustomers.add(depotVertex.neighbours.get(u));
//                edgeWeightsFromDepotToUnservicedCustomers.add(u.neighbours.get(depotVertex));
//            }
//        }
//        if (this.vertex.type == VertexType.CUSTOMER) {
//            edgeWeightsFromDepotToUnservicedCustomers.add(this.vertex.neighbours.get(depotVertex));
//        }
//        Collections.sort(edgeWeightsFromDepotToUnservicedCustomers);
//
//        int vehiclesNeeded = getLowerBoundForNumberOfExtraVehiclesNeeded();
//        if (vehiclesNeeded * 2 > edgeWeightsFromDepotToUnservicedCustomers.size()) {
//            System.out.println("There Is a Bug in BBNode.getMinimumAdditionalTimeNeededToTheEndThePath()!!!!!!!!!!!!!!!!!!!!!!!!");
//            return Integer.MAX_VALUE / 2;
//        }
//
//        if (vehiclesNeeded == 0) return 0;
//
//        return edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 1)
//                + edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 2);
