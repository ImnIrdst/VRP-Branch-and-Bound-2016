import VRP.InputSubsetGenerator;

public class BitSetOpsTest {
    public static void main(String[] args) {
        for (int i=0 ; i<16 ; i++){
            if (i%4 == 0 && i>0) System.out.println();
            System.out.print(InputSubsetGenerator.getBitsetString(i) + " ");
        }

        System.out.println();
        System.out.println(InputSubsetGenerator.countCustomers(14));
        System.out.println(InputSubsetGenerator.countVehicles(14,13));
    }
}
