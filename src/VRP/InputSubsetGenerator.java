package VRP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class InputSubsetGenerator {
    private static int nTries = 10;
    private static int nNodes = 20;
    private static int nVehicles = 0;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/input-subset.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);


        int[][] needs = new int[nNodes][nNodes];

        needs[8][2] = nTries;
        needs[8][3] = nTries;
        needs[8][4] = nTries;
        needs[8][5] = nTries;
        needs[8][6] = nTries;
        needs[8][7] = nTries;
        needs[8][8] = nTries;

        needs[9][2] = nTries;
        needs[9][3] = nTries;
        needs[9][4] = nTries;
        needs[9][5] = nTries;
        needs[9][6] = nTries;
        needs[9][7] = nTries;
        needs[9][8] = nTries;

        needs[10][2] = nTries;
        needs[10][3] = nTries;
        needs[10][4] = nTries;
        needs[10][5] = nTries;
        needs[10][6] = nTries;
        needs[10][7] = nTries;
        needs[10][8] = nTries;

        needs[11][2] = nTries;
        needs[11][3] = nTries;
        needs[11][4] = nTries;
        needs[11][5] = nTries;
        needs[11][6] = nTries;
        needs[11][7] = nTries;
        needs[11][8] = nTries;

        needs[12][2] = nTries;
        needs[12][3] = nTries;
        needs[12][4] = nTries;
        needs[12][5] = nTries;
        needs[12][6] = nTries;
        needs[12][7] = nTries;
        needs[12][8] = nTries;

        int totalTries = 0;
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                totalTries += needs[i][j];
            }
        }

        out.println("nNodes,nCustomers,nVehicles,customers,vehicles");
        int remainedTries = totalTries;
        while (remainedTries > 0) {
            int customersBitSet = getRand();
            int vehiclesBitSet = getRand();

            int cc = countCustomers(customersBitSet);
            int vc = countVehicles(customersBitSet, vehiclesBitSet);

            if (needs[cc][vc] <= 0) continue;

            needs[cc][vc]--;
            remainedTries--;

            out.printf("%d,%d,%d,%d,%d\n", nNodes, cc, vc, customersBitSet, vehiclesBitSet);
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
