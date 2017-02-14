import Main.Algorithms.Other.Random;
import Main.Algorithms.Other.RollingWheel;

/**
 * Created by iman on 2/14/17.
 */
public class RollingWheelTest {
    public static void main(String[] args) {
        double[] probabilities = new double[]{1, 2, 3, 4, 5};

        Random.setSeed(0);
        for (int i = 0; i < 10; i++) {
            System.out.printf("%d %d\n", i, RollingWheel.run(probabilities));
        }
    }
}
