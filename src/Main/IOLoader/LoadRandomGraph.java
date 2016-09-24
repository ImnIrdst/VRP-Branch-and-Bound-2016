package Main.IOLoader;

import Main.Algorithms.Other.Random;
import Main.Graph.Graph;
import Main.Algorithms.Other.Random.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadRandomGraph {
    public static Graph load(long seed) throws FileNotFoundException {
        Scanner sc = new Scanner(new File("resources/Experiments/ex1-automated-test-ranges-01.csv"));

        sc.nextLine();
        String[] minRow = sc.nextLine().split(",");
        String[] maxRow = sc.nextLine().split(",");

        Random.setSeed(seed);
        IRange customerQtyRange = new IRange(Integer.parseInt(minRow[1]), Integer.parseInt(maxRow[1]));
        IRange capacityRange = new IRange(Integer.parseInt(minRow[2]), Integer.parseInt(maxRow[2]));
        IRange vehicleQtyRange = new IRange(Integer.parseInt(minRow[3]), Integer.parseInt(maxRow[3]));
        DRange fixCostRange = new DRange(Double.parseDouble(minRow[4]), Double.parseDouble(maxRow[4]));
        DRange processTimeRange = new DRange(Double.parseDouble(minRow[5]), Double.parseDouble(maxRow[5]));
        DRange dueDateRange = new DRange(Double.parseDouble(minRow[6]), Double.parseDouble(maxRow[6]));
        DRange penaltyRange = new DRange(Double.parseDouble(minRow[7]), Double.parseDouble(maxRow[7]));
        DRange edgeWeightRange = new DRange(Double.parseDouble(minRow[8]), Double.parseDouble(maxRow[8]));

        Graph graph = Graph.buildRandomGraph(
                customerQtyRange, vehicleQtyRange, capacityRange, fixCostRange,
                processTimeRange, dueDateRange, penaltyRange, edgeWeightRange
        );

        return graph;
    }
}
