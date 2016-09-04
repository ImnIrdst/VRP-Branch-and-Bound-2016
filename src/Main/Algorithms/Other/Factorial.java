package Main.Algorithms.Other;

/**
 * Created by iman on 7/27/16.
 */
public class Factorial {
    private static int factSize = 21;
    private static long[] factorial;

    public static long getFactorial(int n) {
        if (factorial == null) {
            factorial = new long[factSize];

            factorial[0] = 1;
            for (int i = 1; i < factSize; i++) {
                factorial[i] = (long) i * factorial[i - 1];
            }
        }
        return factorial[n];
    }

    public static int getFactSize() {
        return factSize;
    }
}
