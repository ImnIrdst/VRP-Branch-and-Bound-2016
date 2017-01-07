package Main.AutomatedTests.TestCases.IntegerTestCase;

import Main.Algorithms.Other.Random.DRange;
import Main.Algorithms.Other.Random.IRange;

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
        int[] fixedCosts = new int[]{1, 10, 100};
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 0.7)};
        IRange[] processTimeRanges = new IRange[]{new IRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] deadLineRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        IRange[] penaltyRanges = new IRange[]{new IRange(1, 5), new IRange(5, 10), new IRange(1, 10)};
        IRange[] edgeWeightsRanges = new IRange[]{new IRange(1, 50), new IRange(50, 100), new IRange(1, 100)};
        IRange[] maxGainRanges = new IRange[]{new IRange(1, 10)};
        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (IRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange deadLineRange : deadLineRanges) {
                                    for (IRange penaltyRange : penaltyRanges) {
                                        for (IRange edgeWeightRange : edgeWeightsRanges) {
                                            for (IRange maxGainRange : maxGainRanges) {
                                                testCases.add(new SCSTestCase(customerQty, vehicleQty, fixCost, capacityRange,
                                                        processTimeRange, dueDateRange, deadLineRange, penaltyRange, edgeWeightRange, maxGainRange));
                                            }
                                        }
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
        int[] vehicleQtys = new int[]{10, 20};
        int[] fixedCosts = new int[]{1, 10, 100};
        DRange[] capacityRanges = new DRange[]{new DRange(0.4, 0.7)};
        IRange[] processTimeRanges = new IRange[]{new IRange(1, 100)};
        DRange[] dueDateRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        DRange[] deadLineRanges = new DRange[]{new DRange(0, 0.5), new DRange(0.5, 1), new DRange(0, 1)};
        IRange[] penaltyRanges = new IRange[]{new IRange(1, 5), new IRange(5, 10), new IRange(1, 10)};
        IRange[] edgeWeightsRanges = new IRange[]{new IRange(1, 50), new IRange(50, 100), new IRange(1, 100)};
        IRange[] maxGainRanges = new IRange[]{new IRange(1, 10)};
        for (int customerQty : customerQtys) {
            for (int vehicleQty : vehicleQtys) {
                for (double fixCost : fixedCosts) {
                    for (DRange capacityRange : capacityRanges) {
                        for (IRange processTimeRange : processTimeRanges) {
                            for (DRange dueDateRange : dueDateRanges) {
                                for (DRange deadLineRange : deadLineRanges) {
                                    for (IRange penaltyRange : penaltyRanges) {
                                        for (IRange edgeWeightRange : edgeWeightsRanges) {
                                            for (IRange maxGainRange : maxGainRanges) {
                                                testCases.add(new SCSTestCase(customerQty, vehicleQty, fixCost, capacityRange,
                                                        processTimeRange, dueDateRange, deadLineRange, penaltyRange, edgeWeightRange, maxGainRange));
                                            }
                                        }
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
