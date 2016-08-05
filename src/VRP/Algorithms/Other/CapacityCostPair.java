package VRP.Algorithms.Other;

/**
 * Makes a pair
 */
public class CapacityCostPair implements Comparable<CapacityCostPair> {
    int capacity;
    double fixedCost;

    public CapacityCostPair(int capacity, double fixedCost) {
        this.capacity = capacity;
        this.fixedCost = fixedCost;
    }

    public double unitCost() {
        return this.fixedCost / this.capacity;
    }

    @Override
    public int compareTo(CapacityCostPair o) {
        return Double.compare(this.unitCost(), o.unitCost());
    }
}
