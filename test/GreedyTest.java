import VRP.Algorithms.Other.Greedy;

import java.util.Arrays;

/**
 * Created by iman on 7/18/16.
 */
public class GreedyTest {
    public static void main(String[] args){
        Integer[] customersDemands = new Integer[]{4,1,2,3,1};
        System.out.println(Greedy.minimumExtraVehiclesNeeded(customersDemands, 2, 5));
    }
}
