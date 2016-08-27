package VRP.AutomatedTests.Table1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;

public class SubsetGenerator {
    private static int nTries = 10;
    private static int nNodes = 10;
    private static int nVehicles = 0;

    public static void main(String[] args) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("resources/t1-input-subset-tmp.csv"));
        PrintWriter out = new PrintWriter(fileOutputStream);


        int[][] needs = new int[nNodes+1][nNodes+1];

        needs[7][2] = nTries;
        needs[7][3] = nTries;
        needs[7][4] = nTries;
        needs[7][5] = nTries;
        needs[7][6] = nTries;
        needs[7][7] = nTries;

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
        needs[9][9] = nTries;

        needs[10][2] = nTries;
        needs[10][3] = nTries;
        needs[10][4] = nTries;
        needs[10][5] = nTries;
        needs[10][6] = nTries;
        needs[10][7] = nTries;
        needs[10][8] = nTries;
        needs[10][9] = nTries;
        needs[10][10] = 2;


        int totalTries = 0;
        for (int i = 0; i < nNodes; i++) {
            for (int j = 0; j < nNodes; j++) {
                totalTries += needs[i][j];
            }
        }

        out.println("Id,nNodes,nCustomers,nVehicles,customers,vehicles");
        int remainedTries = totalTries;
        while (remainedTries > 0) {
            int customersBitSet = getRand();
            int vehiclesBitSet = getRand();

            int cc = countCustomers(customersBitSet);
            int vc = countVehicles(customersBitSet, vehiclesBitSet);

            if (needs[cc][vc] <= 0) continue;

            needs[cc][vc]--;
            remainedTries--;

            out.printf("%d,%d,%d,%d,%d,%d\n", totalTries - remainedTries, nNodes, cc, vc, customersBitSet, vehiclesBitSet);
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
