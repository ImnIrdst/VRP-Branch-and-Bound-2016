package VRP.Algorithms.Other;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Contains Greedy Algorithms
 */
public class Greedy {

    /**
     * calculates how much extra vehicles needed to satisfy remained customers demand
     *
     * @param customerDemands       array of remained customers demand
     * @param otherVehiclesCapacity current vehicle remained capacity
     * @return how much extra vehicles needed to satisfy remained customers demand
     */
    public static int minimumExtraVehicleUsageCostNeeded(
            Integer[] customerDemands, int currentVehicleCapacity, List<CapacityCostPair> otherVehiclesCapacity) {

        Arrays.sort(customerDemands, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });


        int capacityUsed = 0;
        int maximumSatisfiedDemand = 0;
        boolean[] isCustomerServiced = new boolean[customerDemands.length];
        for (int i = 0; i < customerDemands.length; i++) {
            if (capacityUsed >= currentVehicleCapacity) break;
            if (capacityUsed + customerDemands[i] <= currentVehicleCapacity) {
                capacityUsed += customerDemands[i];
                isCustomerServiced[i] = true;
            }
        }

        int sumOfRemainedCustomersDemands = 0;
        for (int i = 0; i < customerDemands.length; i++) {
            if (isCustomerServiced[i] == false) sumOfRemainedCustomersDemands += customerDemands[i];
        }

        return minimumVehicleUsageCostNeeded(sumOfRemainedCustomersDemands, otherVehiclesCapacity);
    }

    /**
     * calculates how much extra vehicles needed to satisfy remained customers demand
     *
     * @param customerDemands                array of remained customers demand
     * @param currentVehicleCapacity         current vehicle remained capacity
     * @param maximumOfOtherVehiclesCapacity other vehicles capacity
     * @return how much extra vehicles needed to satisfy remained customers demand
     */
    public static int minimumExtraVehiclesNeeded(
            Integer[] customerDemands, int currentVehicleCapacity, int maximumOfOtherVehiclesCapacity) {

        Arrays.sort(customerDemands, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });

        int capacityUsed = 0;
        int numberOfServiceCustomers = 0;
        boolean[] isCustomerServiced = new boolean[customerDemands.length];
        for (int i = 0; i < customerDemands.length; i++) {
            if (isCustomerServiced[i] == true) continue;
            if (capacityUsed >= currentVehicleCapacity) break;
            if (capacityUsed + customerDemands[i] <= currentVehicleCapacity) {
                capacityUsed += customerDemands[i];
                numberOfServiceCustomers++;
                isCustomerServiced[i] = true;
            }
        }

        int extraVehicleUsed = 0;
        while (numberOfServiceCustomers < customerDemands.length) {
            capacityUsed = 0;
            extraVehicleUsed++;
            for (int i = 0; i < customerDemands.length; i++) {
                if (isCustomerServiced[i] == true) continue;
                if (capacityUsed >= maximumOfOtherVehiclesCapacity) break;
                if (capacityUsed + customerDemands[i] <= maximumOfOtherVehiclesCapacity) {
                    capacityUsed += customerDemands[i];
                    numberOfServiceCustomers++;
                    isCustomerServiced[i] = true;
                }
            }
        }

        return extraVehicleUsed;
    }

    /**
     * calculates how much extra vehicles needed to satisfy remained customers demand
     *
     * @param sumOfCustomerDemands  summation of remained customers demand
     * @param otherVehiclesCapacity current vehicle remained capacity
     * @return how much extra vehicles needed to satisfy remained customers demand
     */
    private static int minimumVehicleUsageCostNeeded(
            int sumOfCustomerDemands, List<CapacityCostPair> otherVehiclesCapacity) {

        Collections.sort(otherVehiclesCapacity);

        int i = 0;
        int extraVehicleUsageCostNeeded = 0;
        int remainedDemands = sumOfCustomerDemands;
        while (i < otherVehiclesCapacity.size()) {
            if (remainedDemands < otherVehiclesCapacity.get(i).capacity) break;

            remainedDemands -= otherVehiclesCapacity.get(i).capacity;
            extraVehicleUsageCostNeeded += otherVehiclesCapacity.get(i).fixedCost;
            i++;
        }
        if (i < otherVehiclesCapacity.size())
            extraVehicleUsageCostNeeded += remainedDemands * otherVehiclesCapacity.get(i).unitCost();

        return extraVehicleUsageCostNeeded;
    }

}
