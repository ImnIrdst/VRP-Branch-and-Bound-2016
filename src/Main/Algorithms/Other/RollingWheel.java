package Main.Algorithms.Other;

/**
 * Created by iman on 2/14/17.
 */
public class RollingWheel {

    /**
     * return index of the selected probability using rolling wheel algorithm
     */
    public static int run(double[] probabilities) {
        if (probabilities.length == 0) return -1;

        double sumOfAll = 0;
        for (double probability : probabilities) sumOfAll += probability;

        double sum = probabilities[0];
        double rand = Random.getRandomDoubleInRange(new Random.DRange(0, sumOfAll));

        for (int i = 0; i < probabilities.length; i++, sum += probabilities[i]) {
            if (rand < sum) return i;
        }

        return probabilities.length - 1;
    }
}
