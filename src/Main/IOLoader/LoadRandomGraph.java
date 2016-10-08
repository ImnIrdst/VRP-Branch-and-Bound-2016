package Main.IOLoader;

import Main.Algorithms.Other.Random;
import Main.Graph.Graph;
import Main.Algorithms.Other.Random.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class LoadRandomGraph {
    public static final String filePath = "resources/RandomRanges/ex1-automated-test-ranges-01.csv";
    public static Graph loadWithDoubleParams(long seed) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filePath));

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

        Graph graph = Graph.buildRandomGraphDouble(
                customerQtyRange, vehicleQtyRange, capacityRange, fixCostRange,
                processTimeRange, dueDateRange, penaltyRange, edgeWeightRange
        );

        return graph;
    }

    public static Graph loadWithIntParams(long seed) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filePath));

        sc.nextLine();
        String[] minRow = sc.nextLine().split(",");
        String[] maxRow = sc.nextLine().split(",");

        Random.setSeed(seed);
        IRange customerQtyRange = new IRange(Integer.parseInt(minRow[1]), Integer.parseInt(maxRow[1]));
        IRange capacityRange = new IRange(Integer.parseInt(minRow[2]), Integer.parseInt(maxRow[2]));
        IRange vehicleQtyRange = new IRange(Integer.parseInt(minRow[3]), Integer.parseInt(maxRow[3]));
        IRange fixCostRange = new IRange(Integer.parseInt(minRow[4]), Integer.parseInt(maxRow[4]));
        IRange processTimeRange = new IRange(Integer.parseInt(minRow[5]), Integer.parseInt(maxRow[5]));
        IRange dueDateRange = new IRange(Integer.parseInt(minRow[6]), Integer.parseInt(maxRow[6]));
        IRange penaltyRange = new IRange(Integer.parseInt(minRow[7]), Integer.parseInt(maxRow[7]));
        IRange edgeWeightRange = new IRange(Integer.parseInt(minRow[8]), Integer.parseInt(maxRow[8]));

        Graph graph = Graph.buildRandomGraphInt(
                customerQtyRange, vehicleQtyRange, capacityRange, fixCostRange,
                processTimeRange, dueDateRange, penaltyRange, edgeWeightRange
        );

        return graph;
    }
}
