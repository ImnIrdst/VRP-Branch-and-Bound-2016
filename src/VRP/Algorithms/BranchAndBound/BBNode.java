package VRP.Algorithms.BranchAndBound;

import VRP.Algorithms.Other.Greedy;
import VRP.GlobalVars;
import VRP.Graph.Vertex;
import VRP.Graph.VertexType;

import java.util.Arrays;

/**
 * a node that used in branch and bound for VRP
 */
public class BBNode {
    public Vertex vertex;          // current vertex of the graph
    public int vehicleUsed;        // number of vehicle used in this node
    public int curTimeElapsed;     // the time elapsed after moving the vehicle in current path
    public int maxTimeElapsed;     // the maximum time elapsed in all paths
    public int remainedGoods;      // remained goods in the car
    public int cumulativePenaltyTaken; // cumulative penalty taken in all nodes
    public int cumulativeTimeTaken; // cumulative time that all vehicles spend to serve the customers
    public boolean[] servicedNodes; // nodes that are serviced
    public int numberOfServicedCustomers; // for easily terminate the algorithm
    public BBNode parent;          // parent of the node in the BB tree

    public int startTime;          // it's the time when vehicle starts moving
    public int arrivalTime;        // the moment that the vehicle reached to the node
    public int thisVertexPenalty;  // the penalty that taken in this vertex

    /**
     * constructor for the branch and bound node
     */

    public BBNode(Vertex vertex, BBNode parent) {
        this.vertex = vertex;
        this.parent = parent;

        this.calculateVehicleUsed();
        this.calculateCurTimeElapsed();
        this.calculateMaxTimeElapsed();
        this.calculateRemainedGoods();
        this.calculateArrivalTime();
        this.calculateThisVertexPenalty();
        this.calculateCumulativePenaltyTaken();
        this.calculateCumulativeTimeTaken();
        this.calculateServicedNodes();
        this.calculateParentStartTime();

        GlobalVars.numberOfBranchAndBoundNodes++;
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

    /**
     * calculateCurTimeElapsed
     */
    public void calculateCurTimeElapsed() {
        if (parent == null) curTimeElapsed = 0;

        else if (parent.vertex.type == VertexType.DEPOT)
            this.curTimeElapsed = GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);

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
     * calculateRemainedGoods
     */
    public void calculateRemainedGoods() {
        if (parent == null || this.vertex.type == VertexType.DEPOT)
            this.remainedGoods = GlobalVars.vehicleCapacity;
        else if (this.vertex.type == VertexType.CUSTOMER)
            this.remainedGoods = parent.remainedGoods - this.vertex.demand;
        else
            this.remainedGoods = parent.remainedGoods;
    }

    /**
     * calculateArrivalTime
     */
    public void calculateArrivalTime() {
        if (parent == null)
            this.arrivalTime = -1;
        else if (parent.vertex.type == VertexType.DEPOT)
            this.arrivalTime = GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);
            // this.arrivalTime = Math.max(vertex.dueDate, BBUtils.getDistance(parent.vertex, this.vertex));
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

    public void calculateCumulativeTimeTaken() {
        if (parent == null)
            this.cumulativeTimeTaken = 0;
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
            parent.startTime = this.arrivalTime - GlobalVars.bbGraph.getDistance(parent.vertex, this.vertex);

        // for finished nodes.
        if (parent != null && this.vertex.type == VertexType.DEPOT
                && this.numberOfServicedCustomers == GlobalVars.numberOfCustomers)
            this.startTime = -1;
    }


    /**
     * @return cost of the node that we are there
     */
    public int getCost() {            // calculates branch and bound cost of the node
        return cumulativeTimeTaken + cumulativePenaltyTaken + vehicleUsed * GlobalVars.vehicleFixedCost;
    }

    /**
     * Calculates lower bound for this node
     *
     * @return lower bound for this node
     */
    public int getLowerBound() {
        return this.getLowerBoundForPenaltyTaken()
                + this.getLowerBoundForCumulativeTimeNeededForAllVehicles()
                + this.getLowerBoundForNumberOfExtraVehiclesNeeded() * GlobalVars.vehicleFixedCost;
    }


    /**
     * @return minimum number of extra vehicles needed to serve the remaining customers
     */
    public int getLowerBoundForNumberOfExtraVehiclesNeeded() {
        return Greedy.minimumExtraVehiclesNeeded(
                this.getUnservicedCustomersDemands(), this.remainedGoods, GlobalVars.vehicleCapacity
        );
    }

    /**
     * @return a lower bound for cumulative time needed for all the vehicles to serve all customers
     */
    public int getLowerBoundForCumulativeTimeNeededForAllVehicles() {
        int sum = 0;
        for (Vertex v : GlobalVars.bbGraph.getVertices()) {
            if (v.type == VertexType.CUSTOMER
                    && this.servicedNodes[v.customerId] == false) {
                sum += 2 * getMinimumEdgeWeightOfVertex(v);
            }
        }
        return sum;
    }

    /**
     * If go from this vertex to the depot, when I arrive
     * there and how much penalty I must take.
     *
     * @return a lower bound for additional penalty taken
     */
    public int getLowerBoundForPenaltyTaken() {
        if (this.vertex.type == VertexType.DEPOT) return 0;

        int lowestFinishTime = this.curTimeElapsed + this.getMinimumAdditionalTimeNeededToTheEndThePath();

        Vertex depotVertex = GlobalVars.bbGraph.adjacencyList.get(GlobalVars.depotName);

        if (lowestFinishTime > depotVertex.dueDate)
            return (lowestFinishTime - depotVertex.dueDate) * depotVertex.penalty;

        return 0;
    }

    /**
     * @return minimum edge weight of a given vertex
     */
    public int getMinimumEdgeWeightOfVertex(Vertex v) {
        int min = Integer.MAX_VALUE;
        for (Vertex u : v.neighbours.keySet()) {
            if (u.name.equals(v.name)) continue;
            if (!u.name.equals(this.vertex.name)  // TODO: further improvements: only use one edge of current node
                    && u.type == VertexType.CUSTOMER
                    && this.servicedNodes[u.customerId] == true) continue;

            min = Math.min(min, GlobalVars.bbGraph.getDistance(u, v));
        }
        return min;
    }

    /**
     * @return Detail of attributes that affects the cost
     */
    public String getPrintCostDetailsString() {
        return "Time needed: " + maxTimeElapsed + "\n"
                + "Travel Time of all vehicles: " + cumulativeTimeTaken + "\n"
                + "Penalty Taken of all vehicles: " + cumulativePenaltyTaken + "\n"
                + "Number of Vehicles Used: " + vehicleUsed + "\n"
                + "Minimum Cost for the problem: " + getCost();
    }

    /**
     * @return minimum time needed to end this path
     */
    public int getMinimumAdditionalTimeNeededToTheEndThePath() {
        Vertex depotVertex = GlobalVars.bbGraph.adjacencyList.get(GlobalVars.depotName);

        if (this.vertex.type != VertexType.DEPOT)
            return this.vertex.neighbours.get(depotVertex);
        return 0;
    }


    /**
     * @return array of unserviced customers demands
     */
    public Integer[] getUnservicedCustomersDemands() {
        int numberOfUnservicedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
        Integer[] unservicedCustomersDemands = new Integer[numberOfUnservicedCustomers];

        for (int i = 0, j = 0; i < GlobalVars.numberOfCustomers; i++) {
            if (this.servicedNodes[i] == false) unservicedCustomersDemands[j++] = GlobalVars.customerDemands[i];
        }

        return unservicedCustomersDemands;
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

    @Override
    public String toString() {
        if (vertex.type == VertexType.DEPOT && startTime == -1)
            return vertex + " (" + arrivalTime + detailsForToString() + ")";

        else if (vertex.type == VertexType.DEPOT && arrivalTime == -1)
            return vertex + " (" + startTime + ")";

        else if (vertex.type == VertexType.DEPOT)
            return vertex + " (" + arrivalTime + detailsForToString() + ")"
                    + "\n" + vertex + " (" + startTime + ")";

        return vertex + " (" + arrivalTime + detailsForToString() + ")";
    }

    public String detailsForToString() {
        return ", " + thisVertexPenalty + ", " + vertex.dueDate;
    }
}

/* Trash
        ArrayList<Integer> edgeWeightsFromDepotToUnservicedCustomers = new ArrayList<>();

        Vertex depotVertex = GlobalVars.bbGraph.adjacencyList.get(GlobalVars.depotName);
        for (Vertex u : GlobalVars.bbGraph.adjacencyList.values()) {
            if (u.type == VertexType.CUSTOMER && this.servicedNodes[u.customerId] == false) {
                edgeWeightsFromDepotToUnservicedCustomers.add(depotVertex.neighbours.get(u));
                edgeWeightsFromDepotToUnservicedCustomers.add(u.neighbours.get(depotVertex));
            }
        }
        if (this.vertex.type == VertexType.CUSTOMER) {
            edgeWeightsFromDepotToUnservicedCustomers.add(this.vertex.neighbours.get(depotVertex));
        }
        Collections.sort(edgeWeightsFromDepotToUnservicedCustomers);

        int vehiclesNeeded = getLowerBoundForNumberOfExtraVehiclesNeeded();
        if (vehiclesNeeded * 2 > edgeWeightsFromDepotToUnservicedCustomers.size()) {
            System.out.println("There Is a Bug in BBNode.getMinimumAdditionalTimeNeededToTheEndThePath()!!!!!!!!!!!!!!!!!!!!!!!!");
            return Integer.MAX_VALUE / 2;
        }

        if (vehiclesNeeded == 0) return 0;

        return edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 1)
                + edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 2);
*/