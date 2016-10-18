package Main.Algorithms.Heuristics.DispatchingRules;

import Main.Algorithms.TSP.SimpleTSP.SimpleTSP;
import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;
import org.omg.CORBA.StringHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IMN on 10/17/2016.
 */
public class ATC {
    private Graph graph;

    private double result = 0;
    private List<Integer> path;

    public ATC(Graph graph) {
        this.graph = graph;
        path = new ArrayList<>();
    }

    public void run() {
        int servedNodesQty = 0;
        boolean[] isServed = new boolean[graph.getGraphSize()];

        double sumOfProcessTimes = 0;
        for (Vertex v : graph.getCustomerVertices()) sumOfProcessTimes += v.processTime;


        double arrivalTime = 0;
        double remainedCapacity = graph.getDepot().capacity;
        double remainedVehicles = graph.getDepot().vehicleQty - 1;

        Vertex u = graph.getDepot();

        result = u.fixedCost;

        path.add(graph.getDepotId());
        while (servedNodesQty < graph.getGraphSize() - 1) {

            Vertex next = null;
            double bestValue = -GlobalVars.INF;
            for (Vertex v : graph.getCustomerVertices()) {
                if (isServed[v.id] == true) continue;

                double atcValue = getATCRuleValue(u, v, arrivalTime, sumOfProcessTimes);
                if (atcValue > bestValue) {
                    bestValue = atcValue;
                    next = v;
                }
            }

            Vertex start = null;
            double bestValueStart = -GlobalVars.INF;
            for (Vertex v : graph.getCustomerVertices()) {
                if (isServed[v.id] == true) continue;

                double atcValue = getATCRuleValue(graph.getDepot(), v, 0, sumOfProcessTimes);
                atcValue -= getATCRuleValue(u, graph.getDepot(), arrivalTime, 0);
                if (atcValue > bestValueStart) {
                    bestValueStart = atcValue;
                    start = v;
                }
            }


//            double coefficient = 2;
//            bestValueStart -= coefficient * (graph.getDepot().vehicleQty / remainedVehicles) * graph.getDepot().fixedCost;
            if (bestValue > bestValueStart - 1e-9 && remainedCapacity > 0) { // TODO: multiply bestValue by a coefficient
                arrivalTime += graph.getDistance(u, next);
                result += graph.getDistance(u, next);
                result += Math.max(0, (arrivalTime - next.dueDate) * next.penalty);

                u = next;
                path.add(u.id);
                remainedCapacity--;
            } else {
                arrivalTime = graph.getDistance(graph.getDepot(), start);
                result += graph.getDistance(u, graph.getDepot()) + graph.getDistance(graph.getDepot(), start);
                result += Math.max(0, (arrivalTime - start.dueDate) * start.penalty);
                result += graph.getDepot().fixedCost;

                u = start;
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
    }

    public double getATCRuleValue(Vertex u, Vertex v, double previousArrivalTime, double sumOfProcessTimes) {
        double pk = v.processTime;
        double dk = v.dueDate;
        double wk = v.penalty;

        double t = previousArrivalTime;
        double c = graph.getDistance(u, v);
        double A = sumOfProcessTimes;

        double Ik = (wk / pk) - ((dk - pk - t - c) / A) - t;
        return Ik;
    }

    @Override
    public String toString() {
        return path.toString() + ", " + String.format("Result: %.2f", result);
    }
}
