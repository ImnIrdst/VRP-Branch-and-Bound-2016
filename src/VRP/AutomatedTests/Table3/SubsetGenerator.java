package VRP.AutomatedTests.Table3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class SubsetGenerator {
    private static int nTries = 15;
    private static int nNodes = 51;
    private static int nVehicles = 0;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/Table3/t3-input-subset-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);


        int[][] needs = new int[nNodes + 1][nNodes + 1];

        needs[9][9] = nTries;
        needs[10][9] = nTries;
        needs[11][9] = nTries;
        needs[12][9] = nTries;
        needs[15][9] = nTries;
        needs[20][9] = nTries;
        needs[25][9] = nTries;
        needs[30][9] = nTries;
        needs[35][9] = nTries;
        needs[40][9] = nTries;
        needs[45][9] = nTries;
        needs[50][9] = nTries;

        int totalTries = 0;
        for (int i = 0; i < nNodes + 1; i++) {
            for (int j = 0; j < nNodes + 1; j++) {
                totalTries += needs[i][j];
            }
        }

        out.println("Id,nNodes,nCustomers,nVehicles,testType,customers,vehicles");

        int remainedTries = totalTries;
        for (int nc = 0; nc < nNodes + 1; nc++) {
            for (int nv = 0; nv < nNodes + 1; nv++) {
                while (needs[nc][nv]-- > 0) {

                    System.out.printf("nc: %d, nv: %d, rem: %d", nc, nv, needs[nc][nv]);
                    long customersBitSet = getCustomerBitSet(nc);
                    long vehiclesBitSet = getVehicleBitSet(customersBitSet, nv);

//                    System.out.println("Customer BitSet: " + getBitsetString(customersBitSet));
//                    System.out.println("Vehicles BitSet: " + getBitsetString(vehiclesBitSet));

                    remainedTries--;
                    String[] testTypes = new String[]{"Exact", "0.10", "0.40", "0.80"};
                    System.out.printf("%d,%d,%d,%d,%s,%d,%d\n",
                            totalTries - remainedTries, nNodes, nc, nv, 1, customersBitSet, vehiclesBitSet);
                    for (int i = 0; i < testTypes.length; i++) {
                        out.printf("%d,%d,%d,%d,%s,%d,%d\n",
                                totalTries - remainedTries, nNodes, nc, nv, testTypes[i], customersBitSet, vehiclesBitSet);
                    }
                }
            }
        }

        out.close();
    }

    public static int countCustomers(long customersBitSet) {
        int counter = 0;
        for (int i = 0; i < nNodes; i++) {
            if ((customersBitSet & (1L << i)) != 0) counter++;
        }
        return counter;
    }

    public static int countVehicles(long customersBitSet, long vehiclesBitSet) {
        int counter = 0;
        for (int i = 0; i < nNodes; i++) {
            if ((customersBitSet & (1L << i)) != 0
                    && (vehiclesBitSet & (1L << i)) != 0) counter++;
        }
        return counter;
    }

    public static String getBitsetString(long bitSet) {
        String s = "";
        for (int i = 0; i < nNodes; i++) {
            if ((bitSet & (1L << i)) != 0) s = "1" + s;
            else s = "0" + s;
        }
        return s;
    }

    public static long getRand() {
        Random random = new Random();
        return (random.nextInt()) + ((1L << 32) * random.nextInt());
    }

    public static long getRand(int limit) {
        Random random = new Random();
        return random.nextInt(limit);
    }


    public static long getCustomerBitSet(int nc) {
        int cnt = 0;
        int prob = 9;
        long bitSet = 0;
        while (cnt < nc) {
            cnt = 0;
            bitSet = 0;
            for (int i = 0; i < nNodes && cnt < nc; i++) {
                if (getRand(100) < prob) {
                    bitSet |= (1L << i);
                    cnt++;
                }
            }
            if (cnt == nc) return bitSet;
            prob++;
        }
        return 0;
    }

    public static long getVehicleBitSet(long customerBitSet, int nv) {
        int cnt = 0;
        int prob = 9;
        long bitSet = 0;
        while (cnt < nv) {
            cnt = 0;
            bitSet = 0;

            for (int i = 0; i < nNodes && cnt < nv; i++) {
                if (getRand(100) < prob && (customerBitSet & (1L << i)) != 0) {
                    bitSet |= (1L << i);
                    cnt++;
                }
            }
            if (cnt == nv) return bitSet;
            prob++;
        }
        return 0;
    }
}
