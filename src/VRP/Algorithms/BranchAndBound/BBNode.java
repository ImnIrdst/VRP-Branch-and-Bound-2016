package VRP.Algorithms.BranchAndBound;

import VRP.Algorithms.Other.Greedy;
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
    public int penaltyTaken;       // penalty taken for the
    public boolean[] servicedNodes;// nodes that are serviced
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
        this.calculatePenaltyTaken();
        this.calculateServicedNodes();
        this.calculateParentStartTime();

        BBGlobalVariables.numberOfBranchAndBoundNodes++;
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
            this.curTimeElapsed = BBUtils.getDistance(parent.vertex, this.vertex);

        else if (parent.vertex.type == VertexType.CUSTOMER)
            this.curTimeElapsed = parent.curTimeElapsed + BBUtils.getDistance(parent.vertex, this.vertex);

        calculateMaxTimeElapsed();
        if (this.vertex.type == VertexType.DEPOT) curTimeElapsed = 0;
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
            this.remainedGoods = BBGlobalVariables.vehicleCapacity;
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
            this.arrivalTime = BBUtils.getDistance(parent.vertex, this.vertex);
            // this.arrivalTime = Math.max(vertex.dueDate, BBUtils.getDistance(parent.vertex, this.vertex));
        else
            this.arrivalTime = parent.arrivalTime + BBUtils.getDistance(parent.vertex, this.vertex);
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
     * calculatePenaltyTaken
     */
    public void calculatePenaltyTaken() {
        if (parent == null)
            this.penaltyTaken = 0;
        else
            this.penaltyTaken = parent.penaltyTaken + this.thisVertexPenalty;
    }

    /**
     * calculateServicedNodes
     */
    public void calculateServicedNodes() {
        if (parent == null) {
            this.numberOfServicedCustomers = 0;
            this.servicedNodes = new boolean[BBGlobalVariables.numberOfCustomers];
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
            parent.startTime = this.arrivalTime - BBUtils.getDistance(parent.vertex, this.vertex);

        // for finished nodes.
        if (parent != null && this.vertex.type == VertexType.DEPOT
                && this.numberOfServicedCustomers == BBGlobalVariables.numberOfCustomers)
            this.startTime = -1;
    }


    /**
     * @return cost of the node that we are there
     */
    public int getCost() {            // calculates branch and bound cost of the node
        return maxTimeElapsed + penaltyTaken + vehicleUsed * BBGlobalVariables.vehicleFixedCost;
    }

    /**
     * Calculates lower bound for this node
     *
     * @return lower bound for this node
     */
    public int getLowerBound() {
        return this.getLowerBoundForPenaltyTaken()
                + this.getLowerBoundForAdditionalTimeNeededToTheEnd()
                + this.getLowerBoundForNumberOfExtraVehiclesNeeded() * BBGlobalVariables.vehicleFixedCost;
    }


    /**
     * @return minimum number of extra vehicles needed to serve the remaining customers
     */
    public int getLowerBoundForNumberOfExtraVehiclesNeeded() {
        return Greedy.minimumExtraVehiclesNeeded(
                this.getUnservicedCustomersDemands(), this.remainedGoods, BBGlobalVariables.vehicleCapacity
        );
    }

    /**
     * @return a lower bound for time needed to end this path
     */
    public int getLowerBoundForAdditionalTimeNeededToTheEnd() {
        Vertex depotVertex = BBGlobalVariables.graph.adjacencyList.get("Depot");

        if (this.vertex.type != VertexType.DEPOT)
            return this.vertex.neighbours.get(depotVertex);
        return 0;

        /*
        ArrayList<Integer> edgeWeightsFromDepotToUnservicedCustomers = new ArrayList<>();

        Vertex depotVertex = BBGlobalVariables.graph.adjacencyList.get("Depot");
        for (Vertex u : BBGlobalVariables.graph.adjacencyList.values()) {
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
            System.out.println("There Is a Bug in BBNode.getLowerBoundForAdditionalTimeNeededToTheEnd()!!!!!!!!!!!!!!!!!!!!!!!!");
            return Integer.MAX_VALUE / 2;
        }

        if (vehiclesNeeded == 0) return 0;

        return edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 1)
                + edgeWeightsFromDepotToUnservicedCustomers.get(vehiclesNeeded * 2 - 2);
        */
    }

    /**
     * If go from this vertex to the depot, when I arrive
     * there and how much penalty I must take.
     *
     * @return a lower bound for additional penalty taken
     */
    public int getLowerBoundForPenaltyTaken() {
        if (this.vertex.type == VertexType.DEPOT) return 0;

        int lowestFinishTime = this.curTimeElapsed + this.getLowerBoundForAdditionalTimeNeededToTheEnd();

        Vertex depotVertex = BBGlobalVariables.graph.adjacencyList.get("Depot");

        if (lowestFinishTime > depotVertex.dueDate)
            return (lowestFinishTime - depotVertex.dueDate) * depotVertex.penalty;

        return 0;
    }

    /**
     * @return Detail of attributes that affects the cost
     */
    public String getPrintCostDetailsString() {
        return "Time needed: " + maxTimeElapsed + "\n"
                + "Penalty Taken: " + penaltyTaken + "\n"
                + "Number of Vehicles Used: " + vehicleUsed + "\n"
                + "Minimum Cost for the problem: " + getCost();
    }


    /**
     * @return array of unserviced customers demands
     */
    public Integer[] getUnservicedCustomersDemands() {
        int numberOfUnservicedCustomers = BBGlobalVariables.numberOfCustomers - this.numberOfServicedCustomers;
        Integer[] unservicedCustomersDemands = new Integer[numberOfUnservicedCustomers];

        for (int i = 0, j = 0; i < BBGlobalVariables.numberOfCustomers; i++) {
            if (this.servicedNodes[i] == false) unservicedCustomersDemands[j++] = BBGlobalVariables.customerDemands[i];
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
