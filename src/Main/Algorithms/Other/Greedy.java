package Main.Algorithms.Other;

import java.util.Collections;
import java.util.List;

/**
 * Contains Greedy Main.Algorithms
 */
public class Greedy {

    /**
     * calculates how much extra customersVehicle needed to satisfy remained customers demand
     *
     * @param sumOfCustomerDemands  summation of remained customers demand
     * @param otherVehiclesCapacity current vehicle remained capacity
     * @return how much extra customersVehicle needed to satisfy remained customers demand
     */
    public static double minimumExtraVehicleUsageCostNeeded(
            int sumOfCustomerDemands, List<CapacityCostPair> otherVehiclesCapacity) {


        Collections.sort(otherVehiclesCapacity);

        int i = 0;
        double extraVehicleUsageCostNeeded = 0;
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

    /**
     * calculates how much extra customersVehicle needed to satisfy remained customers demand
     *
     * @param sumOfCustomerDemands  summation of remained customers demand
     * @param otherVehiclesCapacity current vehicle remained capacity
     * @return how much extra customersVehicle needed to satisfy remained customers demand
     */
    public static int minimumExtraVehiclesNeeded(
            int sumOfCustomerDemands, List<CapacityCostPair> otherVehiclesCapacity) {

        Collections.sort(otherVehiclesCapacity);

        int i = 0;
        int extraVehicleUsed = -1;
        int remainedDemands = sumOfCustomerDemands;
        while (i < otherVehiclesCapacity.size()) {
            if (remainedDemands < otherVehiclesCapacity.get(i).capacity) break;

            remainedDemands -= otherVehiclesCapacity.get(i).capacity;
            i++; extraVehicleUsed++;
        }
        if (i < otherVehiclesCapacity.size() && remainedDemands > 0)
            extraVehicleUsed++;


        return extraVehicleUsed;
    }
}
