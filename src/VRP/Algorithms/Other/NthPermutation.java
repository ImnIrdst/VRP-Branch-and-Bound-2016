package VRP.Algorithms.Other;

import java.util.*;

import static VRP.Algorithms.Other.Factorial.getFactorial;

public class NthPermutation {
    public static List<Long> getNth(List<Long> original, long permNum) {
        List<Long> numbers = new ArrayList<>(original);
        List<Long> permutation = new ArrayList<>();

        int N = numbers.size();
        for (int i = 1; i < N; ++i) {
            int j = (int) (permNum / getFactorial(N - i));
            permNum = permNum % getFactorial(N - i);
            permutation.add(numbers.get(j));
            numbers.remove(j);

            if (permNum == 0)
                break;
        }

        for (int i = 0; i < numbers.size(); i++)
            permutation.add(numbers.get(i));

        return permutation;
    }
}

