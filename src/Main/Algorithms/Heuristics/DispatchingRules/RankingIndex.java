package Main.Algorithms.Heuristics.DispatchingRules;

import Main.Graph.Vertex;
import Main.Graph.VertexType;

/**
 * Created by IMN on 10/23/2016.
 */
public class RankingIndex {
    public static final int NUMBER_OF_RULES = 5;

    public static double getIndexValue1(Vertex v) {
        if (v.dueDate == 0) return v.dueDate;
        return v.penalty / v.dueDate;
    }

    public static double getIndexValue2(Vertex v) {
        return v.maximumGain;
    }

    public static double getIndexValue3(Vertex v) {
        if (v.maximumGain == 0) return 0;
        return (v.deadline - v.dueDate) / v.maximumGain;
    }

    public static double getIndexValue4(Vertex v) {
        if (v.dueDate == 0) return v.dueDate;
        return (v.maximumGain * v.penalty) / v.dueDate;
    }

    public static double getIndexValue5(Vertex v) {
        if (v.dueDate == 0) return v.dueDate;
        if (v.maximumGain == 0) return 0;
        return ((v.maximumGain * v.penalty) / v.dueDate) * Math.exp((v.deadline - v.dueDate) / v.maximumGain);
    }
}
