package Main.AutomatedTests.SCSTests;

import Main.Algorithms.Other.Random.*;

/**
 * Created by IMN on 10/29/2016.
 */
public class SCSTestCase {
    public int customerQty;
    public int vehicleQty;
    public double fixCost;
    public DRange capacityRange;
    public DRange processTimeRange;
    public DRange dueDateRange;
    public DRange penaltyRange;
    public DRange edgeWeightRange;

    public SCSTestCase(int customerQty, int vehicleQty, double fixCost,
                       DRange capacityRange, DRange processTimeRange, DRange dueDateRange,
                       DRange penaltyRange, DRange edgeWeightRange) {

        this.customerQty = customerQty;
        this.vehicleQty = vehicleQty;
        this.fixCost = fixCost;
        this.capacityRange = capacityRange;
        this.processTimeRange = processTimeRange;
        this.dueDateRange = dueDateRange;
        this.penaltyRange = penaltyRange;
        this.edgeWeightRange = edgeWeightRange;
    }

    public static String getTableHeader(){
        return "customerQty,vehicleQty,fixCost,capacityRange,processTimeRange,dueDateRange,penaltyRange,edgeWeightRange";
    }

    public String getCSVRow() {
        return "" + customerQty + "," + vehicleQty + "," + fixCost + "," + capacityRange + ","
                + processTimeRange + "," + dueDateRange + "," + penaltyRange + "," + edgeWeightRange;
    }
}
