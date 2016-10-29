package Main.AutomatedTests.SCSTests;

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
        int[] customerQtys = new int[]{5, 8, 11};
        int[] vehicleQtys = new int[]{3, 5};
        double[] fixedCosts = new double[]{1, 10, 100};
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 7.0)};
        DRange[] processTimeRanges = new DRange[]{new DRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] penaltyRanges = new DRange[]{new DRange(1, 10), new DRange(45, 55), new DRange(1, 100)};
        DRange[] edgeWeightsRanges = new DRange[]{new DRange(1, 10), new DRange(45, 55), new DRange(1, 100)};

        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (DRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange penaltyRange : penaltyRanges) {
                                    for (DRange edgeWeightRange : penaltyRanges) {
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
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 7.0)};
        DRange[] processTimeRanges = new DRange[]{new DRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] penaltyRanges = new DRange[]{new DRange(1, 10), new DRange(45, 55), new DRange(1, 100)};
        DRange[] edgeWeightsRanges = new DRange[]{new DRange(1, 10), new DRange(45, 55), new DRange(1, 100)};

        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (DRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange penaltyRange : penaltyRanges) {
                                    for (DRange edgeWeightRange : penaltyRanges) {
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
