package Main.Algorithms.Other;


public class Random {
    public static java.util.Random random;

    public static void setSeed(long seed) {
        random = new java.util.Random(seed);
    }

    public static int getRandomIntInRange(IRange range) {
        return range.min + (random.nextInt(range.max - range.min + 1));
    }

    public static double getRandomDoubleInRange(DRange range) {
        return range.min + (range.max - range.min) * random.nextDouble();
    }

    public static class IRange {
        public int min, max;

        public IRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return String.format("[%d    %d]", min, max);
        }
    }

    public static class DRange {
        public double min, max;

        public DRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return String.format("[%.1f   %.1f]", min, max);
        }
    }
}
