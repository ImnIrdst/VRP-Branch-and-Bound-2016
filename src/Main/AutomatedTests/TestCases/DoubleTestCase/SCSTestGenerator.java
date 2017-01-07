package Main.AutomatedTests.TestCases.DoubleTestCase;

import Main.Algorithms.Other.Random.*;

import java.util.LinkedList;
import java.util.Queue;

public class SCSTestGenerator {
    private Queue<SCSTestCase> testCases;

    public SCSTestGenerator() {
        testCases = new LinkedList<>();
    }

    /**
     * Generates Small Test cases
     */
    public void addSmallTestsV1() {
        int[] customerQtys = new int[]{4, 6, 8};
        int[] vehicleQtys = new int[]{3, 4};
        double[] fixedCosts = new double[]{1, 10, 100};
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 0.7)};
        DRange[] processTimeRanges = new DRange[]{new DRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] penaltyRanges = new DRange[]{new DRange(1, 5), new DRange(5, 10), new DRange(1, 10)};
        DRange[] edgeWeightsRanges = new DRange[]{new DRange(1, 50), new DRange(50, 100), new DRange(1, 100)};

        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (DRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange penaltyRange : penaltyRanges) {
                                    for (DRange edgeWeightRange : edgeWeightsRanges) {
                                        testCases.add(new SCSTestCase(customerQty, vehicleQty, fixCost, capacityRange,
                                                processTimeRange, dueDateRange, penaltyRange, edgeWeightRange));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates Big Test Cases
     */
    public void addBigTestsV1() {
        int[] customerQtys = new int[]{20, 40, 60};
        int[] vehicleQtys = new int[]{10, 15, 20};
        double[] fixedCosts = new double[]{1, 10, 100};
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 0.7)};
        DRange[] processTimeRanges = new DRange[]{new DRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] penaltyRanges = new DRange[]{new DRange(1, 5), new DRange(5, 10), new DRange(1, 10)};
        DRange[] edgeWeightsRanges = new DRange[]{new DRange(1, 50), new DRange(50, 100), new DRange(1, 100)};

        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (DRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange penaltyRange : penaltyRanges) {
                                    for (DRange edgeWeightRange : edgeWeightsRanges) {
                                        testCases.add(new SCSTestCase(customerQty, vehicleQty, fixCost, capacityRange,
                                                processTimeRange, dueDateRange, penaltyRange, edgeWeightRange));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true if the queue is not empty
     */
    public boolean hasNextTestCase(){
        return !testCases.isEmpty();
    }

    /**
     * Returns the next test case in the queue.
     */
    public SCSTestCase getNextTestCase() {
        return testCases.poll();
    }

}
