package VRP.AutomatedTests.Table2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class SubsetGenerator {
    private static int nTries = 20;
    private static int nNodes = 20;
    private static int nVehicles = 0;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/t2-input-subset-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);


        int[][] needs = new int[nNodes][nNodes];

        needs[10][9] = nTries;
        needs[12][10] = nTries;


        int totalTries = 0;
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                totalTries += needs[i][j];
            }
        }


        out.println("Id,nNodes,nCustomers,nVehicles,customersBitSet,vehiclesBitSet,fixCost,penaltyCost");


        int[] fixCosts = new int[]{1, 10, 100, 1000};
        int[] penaltyCosts = new int[]{1, 10, 100, 1000};
        int remainedTries = totalTries;
        while (remainedTries > 0) {
            int customersBitSet = getRand();
            int vehiclesBitSet = getRand();

            int cc = countCustomers(customersBitSet);
            int vc = countVehicles(customersBitSet, vehiclesBitSet);

            if (needs[cc][vc] <= 0) continue;

            needs[cc][vc]--;
            remainedTries--;

            for (int fixCost:fixCosts) {
                for (int penaltyCost: penaltyCosts) {
                    out.printf("%d,%d,%d,%d,%d,%d,%d,%d\n",
                            totalTries - remainedTries, nNodes, cc, vc, customersBitSet, vehiclesBitSet, fixCost, penaltyCost);
                }
            }

        }

        out.close();
    }

    public static int countCustomers(int customersBitSet) {
        int counter = 0;
        for (int i = 0; i < nNodes; i++) {
            if ((customersBitSet & (1 << i)) != 0) counter++;
        }
        return counter;
    }

    public static int countVehicles(int customersBitSet, int vehiclesBitSet) {
        int counter = 0;
        for (int i = 0; i < nNodes; i++) {
            if ((customersBitSet & (1 << i)) != 0
                    && (vehiclesBitSet & (1 << i)) != 0) counter++;
        }
        return counter;
    }

    public static String getBitsetString(int bitSet) {
        String s = "";
        for (int i = 0; i < nNodes; i++) {
            if ((bitSet & (1 << i)) != 0) s = "1" + s;
            else s = "0" + s;
        }
        return s;
    }

    public static int getRand() {
        Random random = new Random();
        return random.nextInt(1 << nNodes);
    }

}
