package VRP.Algorithms.Other;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Contains Greedy Algorithms
 */
public class Greedy {

    /**
     * calculates how much extra vehicles needed to satisfy remained customers demand
     *
     * @param customerDemands        array of remained customers demand
     * @param currentVehicleCapacity current vehicle remained capacity
     * @param otherVehiclesCapacity  other vehicles capacity
     * @return how much extra vehicles needed to satisfy remained customers demand
     */
    public static int minimumExtraVehiclesNeeded(Integer[] customerDemands, int currentVehicleCapacity, int otherVehiclesCapacity) {
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
                if (capacityUsed >= otherVehiclesCapacity) break;
                if (capacityUsed + customerDemands[i] <= otherVehiclesCapacity) {
                    capacityUsed += customerDemands[i];
                    numberOfServiceCustomers++;
                    isCustomerServiced[i] = true;
                }
            }
        }

        return extraVehicleUsed;
    }
}
