package Main.Algorithms.TSP.SimpleTSP;

import Main.GlobalVars;
import Main.Graph.Graph;
import Main.Graph.Vertex;

import java.util.List;

public class SimpleTSP {
    private Graph graph;
    private List<Integer> waitingList;

    public double startTime;
    public double arrivalTime;
    public double travelTime;
    public double penaltyTaken;

    public SimpleTSP(Graph graph, List<Integer> waitingList, double startTime) {
        this.graph = graph;
        this.waitingList = waitingList;
        this.startTime = startTime;
    }

    public void run() {
        this.arrivalTime = this.startTime;
        for (int i = 0; i < waitingList.size(); i++) {
            Vertex current = graph.getVertexById(waitingList.get(i));
            Vertex previous = graph.getDepot();
            if (i != 0) previous = graph.getVertexById(waitingList.get(i - 1));

            this.arrivalTime += graph.getDistance(previous, current);
            if (this.arrivalTime <= current.deadline)
                this.penaltyTaken += Math.max(this.arrivalTime - current.dueDate, 0) * current.penalty;
            else
                this.penaltyTaken += GlobalVars.INF;
        }

        this.travelTime = this.arrivalTime - this.startTime;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("");
        sb.append(String.format("%s(%.2f, %.2f, %.2f)", graph.getDepot(), startTime, 0.0, travelTime));

        double tmpPenaltyTaken = 0;
        double tmpArrivalTime = this.startTime;
        for (int i = 0; i < waitingList.size(); i++) {
            Vertex current = graph.getVertexById(waitingList.get(i));
            Vertex previous = graph.getDepot();
            if (i != 0) previous = graph.getVertexById(waitingList.get(i - 1));

            tmpArrivalTime += graph.getDistance(previous, current);
            tmpPenaltyTaken += Math.max(tmpArrivalTime - current.dueDate, 0) * current.penalty;

            sb.append(String.format(" -> %s(%.2f, %.2f, %.2f)",
                    current, tmpArrivalTime, current.dueDate,
                    Math.max(tmpArrivalTime - current.dueDate, 0) * current.penalty)
            );
        }
        return sb.toString();
    }

}
