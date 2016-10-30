package Main.Algorithms.Heuristics.DispatchingRules;

import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import Main.Graph.VertexType;

import java.util.ArrayList;
import java.util.List;

public class ATC {
    private Graph graph;

    private double result = 0;
    private List<Integer> path;

    List<Integer> S, T, N, Np, U;
    int v;
    double A;

    double arrivalTime;
    Integer prevVertexId;

    private long startTime;
    private long finishTime;

    public ATC(Graph graph) {
        this.graph = graph;
        path = new ArrayList<>();
    }

    public void run() {
        startTime = System.currentTimeMillis();

        int servedNodesQty = 0;
        boolean[] isServed = new boolean[graph.getGraphSize()];

        double sumOfProcessTimes = 0;
        for (Vertex v : graph.getCustomerVertices()) sumOfProcessTimes += v.processTime;

        double arrivalTime = 0;
        double currentVehiclePenalties = 0;
        double vehicleFixedCost = graph.getDepot().fixedCost;
        double remainedCapacity = graph.getDepot().capacity;
        double remainedVehicles = graph.getDepot().vehicleQty - 1;

        Vertex u = graph.getDepot();

        result = u.fixedCost;

        path.add(graph.getDepotId());
        while (servedNodesQty < graph.getGraphSize() - 1) {
            arrivalTime = u.processTime;

            Vertex next = null;
            double bestValue = -GlobalVars.INF;
            for (Vertex v : graph.getCustomerVertices()) {
                if (isServed[v.id] == true) continue;

                double atcValue = RankingIndex.getIndexValue3(
                        u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
                if (atcValue > bestValue) {
                    bestValue = atcValue;
                    next = v;
                }
            }


            double newStartPenalty = Math.max(0, (graph.getDistance(graph.getDepot(), next) - next.dueDate) * next.penalty);
            double currentPenalty = Math.max(0, (arrivalTime + graph.getDistance(u, next) - next.dueDate) * next.penalty);
            if ((currentVehiclePenalties + currentPenalty < vehicleFixedCost + newStartPenalty && remainedCapacity > 0)
                    || remainedVehicles <= 0) { // TODO: multiply bestValue by a coefficient
                arrivalTime += graph.getDistance(u, next);
                result += graph.getDistance(u, next);
                result += currentPenalty;

                u = next;
                path.add(u.id);
                remainedCapacity--;
            } else {
                arrivalTime = graph.getDistance(graph.getDepot(), next);
                result += graph.getDistance(u, graph.getDepot()) + graph.getDistance(graph.getDepot(), next);
                result += Math.max(0, (arrivalTime - next.dueDate) * next.penalty);
                result += graph.getDepot().fixedCost;

                u = next;
                path.add(graph.getDepotId());
                path.add(u.id);

                remainedCapacity = graph.getDepot().capacity - 1;
                remainedVehicles--;
            }

            isServed[u.id] = true;
            servedNodesQty++;

            if (servedNodesQty == graph.getGraphSize() - 1) {
                result += graph.getDistance(u, graph.getDepot());
                path.add(graph.getDepotId());
            }
        }

        List<Integer> list = new ArrayList<>(path);
        List<Integer> waitingList = new ArrayList<>();

        Vertex depot = graph.getDepot();
        double penaltyCost = 0;
        double vehiclesUsageCost = 0;
        double travelTimeCost = 0;
        double cumulativeProcessTime = 0;
        for (int i = 0; i < list.size(); i++) {
            Vertex v = graph.getVertexById(list.get(i));

            if (v.type == VertexType.CUSTOMER) {
                waitingList.add(v.id);
                cumulativeProcessTime += v.processTime;
            }

            if (waitingList.size() > depot.capacity) {
                this.result = GlobalVars.INF;
            }

            if (v.type == VertexType.DEPOT && waitingList.size() > 0) {
                waitingList.add(depot.getId());
                SimpleTSP tsp = new SimpleTSP(graph, waitingList, cumulativeProcessTime);
                tsp.run();

                vehiclesUsageCost += depot.fixedCost;
                travelTimeCost += tsp.travelTime;
                penaltyCost += tsp.penaltyTaken;

                waitingList.clear();
            }
        }

        if (waitingList.size() > 0) {
            waitingList.add(depot.getId());
            SimpleTSP tsp = new SimpleTSP(graph, waitingList, cumulativeProcessTime);
            tsp.run();

            vehiclesUsageCost += depot.fixedCost;
            travelTimeCost += tsp.travelTime;
            penaltyCost += tsp.penaltyTaken;

            waitingList.clear();
        }

        this.result = vehiclesUsageCost + travelTimeCost + penaltyCost;

        finishTime = System.currentTimeMillis();
    }

    public double getElapsedTimeInSeconds() {
        return (finishTime - startTime) / 1000.0;
    }

    public double getCost() {
        return result;
    }

    @Override
    public String toString() {
        return path.toString() + ", " + String.format("Result: %.2f", result);
    }
}

//    int servedNodesQty = 0;
//        boolean[] isServed = new boolean[graph.getGraphSize()];
//
//        double sumOfProcessTimes = 0;
//        for (Vertex v : graph.getCustomerVertices()) sumOfProcessTimes += v.processTime;
//
//
//        double arrivalTime = 0;
//        double remainedCapacity = graph.getDepot().capacity;
//        double remainedVehicles = graph.getDepot().vehicleQty - 1;
//
//        Vertex u = graph.getDepot();
//
//        result = u.fixedCost;
//
//        path.add(graph.getDepotId());
//        while (servedNodesQty < graph.getGraphSize() - 1) {
//
//            Vertex next = null;
//            double bestValue = -GlobalVars.INF;
//            for (Vertex v : graph.getCustomerVertices()) {
//                if (isServed[v.id] == true) continue;
//
//                double atcValue = RankingIndex.getIndexValue2(
//                        u, v, graph.getDistance(u, v), arrivalTime, sumOfProcessTimes);
//                if (atcValue > bestValue) {
//                    bestValue = atcValue;
//                    next = v;
//                }
//            }
//
//            Vertex start = null;
//            double bestValueStart = -GlobalVars.INF;
//            for (Vertex v : graph.getCustomerVertices()) {
//                if (isServed[v.id] == true) continue;
//
//                double atcValue = RankingIndex.getIndexValue2(
//                        graph.getDepot(), v, graph.getDistance(graph.getDepot(), v), 0, sumOfProcessTimes);
//                atcValue -= RankingIndex.getIndexValue2(
//                        u, graph.getDepot(), graph.getDistance(u, graph.getDepot()), arrivalTime, sumOfProcessTimes);
//                if (atcValue > bestValueStart) {
//                    bestValueStart = atcValue;
//                    start = v;
//                }
//            }
//
//
////            double coefficient = 2;
////            bestValueStart -= coefficient * (graph.getDepot().vehicleQty / remainedVehicles) * graph.getDepot().fixedCost;
//            if (bestValue > bestValueStart - 1e-9 && remainedCapacity > 0) { // TODO: multiply bestValue by a coefficient
//                arrivalTime += graph.getDistance(u, next);
//                result += graph.getDistance(u, next);
//                result += Math.max(0, (arrivalTime - next.dueDate) * next.penalty);
//
//                u = next;
//                path.add(u.id);
//                remainedCapacity--;
//            } else {
//                arrivalTime = graph.getDistance(graph.getDepot(), start);
//                result += graph.getDistance(u, graph.getDepot()) + graph.getDistance(graph.getDepot(), start);
//                result += Math.max(0, (arrivalTime - start.dueDate) * start.penalty);
//                result += graph.getDepot().fixedCost;
//
//                u = start;
//                path.add(graph.getDepotId());
//                path.add(u.id);
//
//                remainedCapacity = graph.getDepot().capacity - 1;
//                remainedVehicles--;
//            }
//
//            isServed[u.id] = true;
//            servedNodesQty++;
//
//            if (servedNodesQty == graph.getGraphSize() - 1) {
//                result += graph.getDistance(u, graph.getDepot());
//                path.add(graph.getDepotId());
//            }
//        }