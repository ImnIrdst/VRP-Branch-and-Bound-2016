package Main.Algorithms.SupplyChainScheduling.BeamSearch;

import Main.Algorithms.Other.TimeIdPair;
import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Edge;
import Main.Graph.Vertex;
import Main.Graph.VertexType;
import org.omg.CORBA.INTERNAL;

import java.beans.Customizer;
import java.util.*;

/**
 * a node that used in branch and bound for VRP
 */
public class BSNode {
    public Vertex vertex;          // current vertex of the graph
    public BSNode parent;          // parent of the node in the BBAutoTest tree

    public List<Integer> waitingList; // customers that must be served
    public double cumulativeProcessTime; // process time of served customers
    public double cumulativeWLTravelTime; // cumulative travel time of waiting customers customers

    public int vehicleUsed;        // number of vehicle used in this node
    public double vehicleUsageCost;
    public double curTimeElapsed;     // the time elapsed after moving the vehicle in current path
    public double maxTimeElapsed;     // the maximum time elapsed in all paths
    public double cumulativePenaltyTaken; // cumulative penalty taken in all nodes
    public double cumulativeTimeTaken; // cumulative time that all customersVehicle spend to serve the customers
    public double cumulativeWLMinimum2ndEdges; // cumulative waiting customers minimum second edges
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
    public double lowerBoundForSimplerProblem;

    /**
     * Default Constructor (Empty Constructor)
     */
    public BSNode() {
    }

    /**
     * constructor for the branch and bound node
     */
    public BSNode(Vertex vertex, BSNode parent) {
        this.vertex = vertex;
        this.parent = parent;

        this.calculateServicedNodes();
        this.calculateWaitingList();
        this.calculateCumulativeProcessTime();
        this.calculateCumulativeWLTravelTime();
        this.calculateCumulativeWLMinimumSecondEdges();
        this.calculateTsp();

        this.calculateVehicleUsed();
        this.calculateVehicleUsageCost();
        this.calculateCurTimeElapsed();
        this.calculateRemainedCapacity();
        this.calculateCumulativePenaltyTaken();
        this.calculateCumulativeTimeTaken();


        this.calculateLowerBoundForPenaltyTaken();
        this.calculateLowerBoundForMinimumVehicleUsageCost();
        this.calculateLowerBoundForTravelTime();
        this.calculateLowerBoundForSimplerProblem();

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

    /**
     * calculateCumulativeProcessTime
     */
    public void calculateCumulativeProcessTime() {
        if (parent == null)
            cumulativeProcessTime = 0;
        else if (this.vertex.type == VertexType.CUSTOMER)
            cumulativeProcessTime += parent.cumulativeProcessTime + this.vertex.processTime;
        else
            cumulativeProcessTime += parent.cumulativeProcessTime;
    }

    /**
     * calculateCumulativeWLTravelTime
     */
    public void calculateCumulativeWLTravelTime() {
        if (parent != null && parent.vertex.type == VertexType.DEPOT)
            this.cumulativeWLTravelTime += GlobalVars.ppGraph.getDistance(parent.vertex, this.vertex);
        if (parent != null && parent.vertex.type != VertexType.DEPOT)
            this.cumulativeWLTravelTime += parent.cumulativeWLTravelTime
                    + GlobalVars.ppGraph.getDistance(parent.vertex, this.vertex);
    }

    public void calculateCumulativeWLMinimumSecondEdges() {
        if (parent != null && parent.vertex.type == VertexType.DEPOT)
            this.cumulativeWLMinimum2ndEdges += getSecondMinimumEdgeWeightOfVertex(this.vertex);
        if (parent != null && parent.vertex.type != VertexType.DEPOT)
            this.cumulativeWLMinimum2ndEdges += parent.cumulativeWLMinimum2ndEdges
                    + getSecondMinimumEdgeWeightOfVertex(this.vertex);
    }

    /**
     * calculateTsp
     */
    private void calculateTsp() {
        if (parent != null && this.vertex.type == VertexType.DEPOT) {
            this.tsp = new SimpleTSP(GlobalVars.ppGraph, this.waitingList, this.cumulativeProcessTime);
            this.tsp.run();
//            this.waitingList.clear();
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
//        int remainedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
//        int vehiclesNeeded = (remainedCustomers - this.remainedCapacity) / GlobalVars.depot.capacity;
//
//        lowerBoundForVehicleCost = vehiclesNeeded * GlobalVars.depot.fixedCost;
    }

    /**
     * first pick depot second minimum edges,
     * then pick remained customers second minimum edges
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

        Vertex u = GlobalVars.depot;
        for (int i = 0; i < waitingList.size() && vertex.type != VertexType.DEPOT; i++) {
            Vertex v = GlobalVars.ppGraph.getVertexById(waitingList.get(i));
            lowerBound += GlobalVars.ppGraph.getDistance(u, v);

            u = v;
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
//        if (parent == null) {
//            lowerBoundForPenaltyTaken = 0;
//            for (Vertex v : GlobalVars.ppGraph.getVertices()) {
//                double minimumArrivalTime = getMinimumEdgeWeightOfVertex(v) + v.processTime;
//                lowerBoundForPenaltyTaken += Math.max(0, minimumArrivalTime - v.dueDate) * v.penalty;
//            }
//        } else
//            lowerBoundForPenaltyTaken = parent.lowerBoundForPenaltyTaken;
//
//        if (vertex.type == VertexType.CUSTOMER) {
//            double minimumArrivalTime = getMinimumEdgeWeightOfVertex(vertex) + vertex.processTime;
//            lowerBoundForPenaltyTaken -= Math.max(0, minimumArrivalTime - vertex.dueDate) * vertex.penalty;
//        }
    }

    /**
     * calculateLowerBoundForSimplerProblem
     */
    public void calculateLowerBoundForSimplerProblem() {
        lowerBoundForSimplerProblem = GlobalVars.INF;

        double minimumEdgeWeight = GlobalVars.INF;
        double minimumPenalty = GlobalVars.INF;
        double minimumProcessTime = GlobalVars.INF;

        // extract unserved customers
        List<Vertex> unservedCustomers = new ArrayList<>();
        for (Vertex v : GlobalVars.ppGraph.getVertices()) {
            if (v.type != VertexType.CUSTOMER || this.servicedNodes[v.id] == true) continue;
            unservedCustomers.add(v);

            minimumEdgeWeight = Math.min(minimumEdgeWeight, getMinimumEdgeWeightOfVertex(v));
            minimumPenalty = Math.min(minimumPenalty, v.penalty);
            minimumProcessTime = Math.min(minimumProcessTime, v.processTime);
        }

        if (unservedCustomers.size() == 0) {
            lowerBoundForSimplerProblem = 0;
            return;
        }

        if (this.vertex.type != VertexType.DEPOT) for (Integer vId: waitingList){
            Vertex v = GlobalVars.ppGraph.getVertexById(vId);

            minimumEdgeWeight = Math.min(minimumEdgeWeight, getMinimumEdgeWeightOfVertex(v));
            minimumPenalty = Math.min(minimumPenalty, v.penalty);
            minimumProcessTime = Math.min(minimumProcessTime, v.processTime);
        }

        // sort them by due dates
        Collections.sort(unservedCustomers, new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return Double.compare(o1.dueDate, o2.dueDate);
            }
        });


        // calculate optimal cost
        double optimalCost = GlobalVars.INF;
        for (int vehicleQty = 1; vehicleQty <= GlobalVars.numberOfVehicles - this.vehicleUsed; vehicleQty++) {
            if (vehicleQty * GlobalVars.depot.fixedCost >= optimalCost) break;

            // initialization
            List<Vertex>[] vehicleLoads = new ArrayList[vehicleQty];
            List<Double>[] penalties = new ArrayList[vehicleQty]; // TODO: use for increasing bounds
            List<Double>[] processTimes = new ArrayList[vehicleQty];
            PriorityQueue<TimeIdPair> timeIdPQ = new PriorityQueue<>(10);
            for (int i = 0; i < vehicleQty; i++) {
                vehicleLoads[i] = new ArrayList<>();
                penalties[i] = new ArrayList<>(); // TODO: use for increasing bounds
                processTimes[i] = new ArrayList<>();
                if (i != 0) timeIdPQ.add(new TimeIdPair(0, i));
            }

            // addCustomer waiting customers vertexes to vehicle
            double tempTime = 0;
            for (int i = 0; i < waitingList.size() && this.vertex.type != VertexType.DEPOT; i++) {
                Vertex tempVertex = GlobalVars.ppGraph.getVertexById(waitingList.get(i));
                vehicleLoads[0].add(tempVertex);
                penalties[0].add(minimumPenalty); // TODO: use for increasing bounds
                processTimes[0].add(minimumProcessTime);

                tempTime += minimumEdgeWeight + minimumProcessTime;
            }
            timeIdPQ.add(new TimeIdPair(tempTime, 0));


            // addCustomer unserved customers to customersVehicle
            boolean isOverLoaded = false;
            for (int i = 0; i < unservedCustomers.size(); i++) {
                TimeIdPair timeIdPair = timeIdPQ.poll();

                vehicleLoads[timeIdPair.id].add(unservedCustomers.get(i));
                penalties[timeIdPair.id].add(minimumPenalty); // TODO: use for increasing bounds
                processTimes[timeIdPair.id].add(minimumProcessTime);

                if (vehicleLoads[timeIdPair.id].size() > GlobalVars.depot.capacity) {
                    isOverLoaded = true;
                    break;
                }

                timeIdPair.time += minimumEdgeWeight + minimumProcessTime;

                timeIdPQ.add(timeIdPair);
            }
            if (isOverLoaded == true) continue; // if overloaded don't updates optimal cost

            // adjust penalties // TODO: use for increasing bounds
            for (int i = 0; i < vehicleQty; i++) {
                for (int j = 0; j < vehicleLoads[i].size(); j++) {
                    if (j == 0) penalties[i].set(j, vehicleLoads[i].get(j).penalty);
                    if (j != 0) penalties[i].set(j, Math.min(penalties[i].get(j - 1), vehicleLoads[i].get(j).penalty));
                }
            }

            // adjust process times
            double[] sumOfProcessTimes = new double[vehicleQty]; // TODO: use for increasing bounds
            for (int i = 0; i < vehicleQty; i++) {
                for (int j = vehicleLoads[i].size() - 1; j >= 0; j--) {
                    if (j == vehicleLoads[i].size() - 1)
                        processTimes[i].set(j, vehicleLoads[i].get(j).processTime);
                    if (j != vehicleLoads[i].size() - 1)
                        processTimes[i].set(j, Math.min(processTimes[i].get(j + 1), vehicleLoads[i].get(j).processTime));

                    sumOfProcessTimes[i] += processTimes[i].get(j);
                }
            }


            // calculate cost
            double vehicleUsageCost = vehicleQty * GlobalVars.depot.fixedCost;
            double travelTimeCost = 0;
            double penaltyCost = 0;
            for (int i = 0; i < vehicleQty; i++) {
                double timeline = vehicleLoads[i].size() * minimumProcessTime;// sumOfProcessTimes[i]; TODO: use for increasing bounds
                for (int j = 0; j < vehicleLoads[i].size(); j++) {
                    timeline += minimumEdgeWeight;
                    travelTimeCost += minimumEdgeWeight; // TODO: use alternative lowerBound

                    penaltyCost += Math.max(0, (timeline - vehicleLoads[i].get(j).dueDate)) * minimumPenalty ; // * penalties[i].getCustomer(j); TODO: use for increasing bounds
                }

                travelTimeCost += minimumEdgeWeight; // TODO: use alternative lowerBound
            }

            if (lowerBoundForTravelTime > travelTimeCost){
//                System.out.println("lowerBoundForTravelTime > travelTimeCost");
                travelTimeCost = lowerBoundForTravelTime;
            }

            double currentCost = vehicleUsageCost + travelTimeCost + penaltyCost;
            optimalCost = Math.min(optimalCost, currentCost);
        }
        if (optimalCost > GlobalVars.INF - 10)
            lowerBoundForSimplerProblem = 0;
        else
            lowerBoundForSimplerProblem = optimalCost;
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
        return lowerBoundForVehicleCost /*+ lowerBoundForTravelTime*/ + lowerBoundForPenaltyTaken + lowerBoundForSimplerProblem;
    }

    // --------------   helper functions ---------------

    /**
     * @return minimum number of extra customersVehicle needed to serve the remaining customers
     */
    public int getMinimumNumberOfExtraVehiclesNeeded() {
        int remainedCustomers = GlobalVars.numberOfCustomers - this.numberOfServicedCustomers;
        return (int) (Math.ceil((remainedCustomers - this.remainedCapacity) / (double) GlobalVars.depot.capacity) + 1e-6);
    }

    /**
     * @return minimum edge weight of a given vertex
     */
    public double getSecondMinimumEdgeWeightOfVertex(Vertex v) {
        double min = Integer.MAX_VALUE;

        for (Vertex u : v.neighbours.keySet()) {
            if (u.id == v.id) continue;
            if (u.type == VertexType.CUSTOMER
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

        for (Vertex u : v.neighbours.keySet()) {
            if (u.id == v.id) continue;
            if (u.type == VertexType.CUSTOMER && this.servicedNodes[u.id] == true) continue;

            if (min > GlobalVars.ppGraph.getDistance(u, v)) {
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
        for (BSNode node = this; node != null; node = node.parent) {
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
                + "Travel Time of all customersVehicle: " + String.format("%.2f", cumulativeTimeTaken) + "\n"
                + "Penalty Taken of all customersVehicle: " + String.format("%.2f", cumulativePenaltyTaken) + "\n"
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
//        Vertex depotVertex = Main.GlobalVars.ppGraph.adjacencyList.getCustomer(Main.GlobalVars.depotName);
//        for (Vertex u : Main.GlobalVars.ppGraph.adjacencyList.values()) {
//            if (u.type == VertexType.CUSTOMER && this.servicedNodes[u.id] == false) {
//                edgeWeightsFromDepotToUnservicedCustomers.addCustomer(depotVertex.neighbours.getCustomer(u));
//                edgeWeightsFromDepotToUnservicedCustomers.addCustomer(u.neighbours.getCustomer(depotVertex));
//            }
//        }
//        if (this.vertex.type == VertexType.CUSTOMER) {
//            edgeWeightsFromDepotToUnservicedCustomers.addCustomer(this.vertex.neighbours.getCustomer(depotVertex));
//        }
//        Collections.sort(edgeWeightsFromDepotToUnservicedCustomers);
//
//        int vehiclesNeeded = getMinimumNumberOfExtraVehiclesNeeded();
//        if (vehiclesNeeded * 2 > edgeWeightsFromDepotToUnservicedCustomers.size()) {
//            System.out.println("There Is a Bug in BSNode.getMinimumAdditionalTimeNeededToTheEndThePath()!!!!!!!!!!!!!!!!!!!!!!!!");
//            return Integer.MAX_VALUE / 2;
//        }
//
//        if (vehiclesNeeded == 0) return 0;
//
//        return edgeWeightsFromDepotToUnservicedCustomers.getCustomer(vehiclesNeeded * 2 - 1)
//                + edgeWeightsFromDepotToUnservicedCustomers.getCustomer(vehiclesNeeded * 2 - 2);
