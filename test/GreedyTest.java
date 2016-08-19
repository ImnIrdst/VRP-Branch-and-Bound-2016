import VRP.Algorithms.Other.CapacityCostPair;
import VRP.Algorithms.Other.Greedy;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests Greedy Algorithms
 */
public class GreedyTest {
    public static void main(String[] args) {

        int sumOfCustomersDemands = 7 + 4 + 3 + 2 + 1 + 1;
        Integer[] customerDemands = {7, 4, 3, 2, 1, 1};
        List<CapacityCostPair> vehiclesCapacity = new ArrayList<>();

        vehiclesCapacity.add(new CapacityCostPair(6, 12));
        vehiclesCapacity.add(new CapacityCostPair(8, 16));
        vehiclesCapacity.add(new CapacityCostPair(5, 0));

        System.out.println(Greedy.minimumExtraVehicleUsageCostNeeded(sumOfCustomersDemands, vehiclesCapacity));
        System.out.println(Greedy.minimumExtraVehiclesNeeded(sumOfCustomersDemands, vehiclesCapacity));
    }
}
