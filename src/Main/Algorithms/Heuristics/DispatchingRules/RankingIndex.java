package Main.Algorithms.Heuristics.DispatchingRules;

import Main.Graph.Vertex;
import Main.Graph.VertexType;

/**
 * Created by IMN on 10/23/2016.
 */
public class RankingIndex {
    public static final int NUMBER_OF_RULES = 4;

    public static double getIndexValue1(
            Vertex u, Vertex v, double distanceUV, double previousArrivalTime, double sumOfProcessTimes) {

        double pk = v.processTime;
        double dk = v.dueDate;
        double wk = v.penalty;

        if (v.type == VertexType.DEPOT) pk = 1;

        double t = previousArrivalTime;
        double c = distanceUV;
        double A = sumOfProcessTimes;

        double Ik = -(wk * dk);
        return Ik;
    }

    public static double getIndexValue2(
            Vertex u, Vertex v, double distanceUV, double previousArrivalTime, double sumOfProcessTimes) {

        double pk = v.processTime;
        double dk = v.dueDate;
        double wk = v.penalty;

        if (v.type == VertexType.DEPOT) pk = 1;

        double t = previousArrivalTime;
        double c = distanceUV;
        double A = sumOfProcessTimes;

        double Ik = (wk / pk) - ((dk - pk - t - c) / A) - t;
        return Ik;
    }

    public static double getIndexValue3(
            Vertex u, Vertex v, double distanceUV, double previousArrivalTime, double sumOfProcessTimes) {

        double pk = v.processTime;
        double dk = v.dueDate;
        double wk = v.penalty;

        if (v.type == VertexType.DEPOT) pk = 1;

        double t = previousArrivalTime;
        double c = distanceUV;
        double A = sumOfProcessTimes;

        double Ik = (wk / pk) * Math.exp(-Math.max(dk - pk - t - c, 0) / A);
        return Ik;
    }

    public static double getIndexValue4(
            Vertex u, Vertex v, double distanceUV, double previousArrivalTime, double sumOfProcessTimes) {

        double pk = v.processTime;
        double dk = v.dueDate;
        double wk = v.penalty;

        if (v.type == VertexType.DEPOT) pk = 1;

        double t = previousArrivalTime;
        double c = distanceUV;
        double A = sumOfProcessTimes;

        double Ik = -(pk);
        return Ik;
    }
}
