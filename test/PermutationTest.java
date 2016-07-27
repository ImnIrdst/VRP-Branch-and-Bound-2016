import VRP.Algorithms.Other.NthPermutation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iman on 7/27/16.
 */
public class PermutationTest {
    public static void main(String[] args){
        List<Long> numbers = new ArrayList<>();
        for (long i = 0; i <3; i++) {
            numbers.add(i);
        }
        for (long i = 0; i < 6; i++) {
            System.out.println(NthPermutation.getNth(numbers, i));
        }
    }
}
