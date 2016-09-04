import Main.AutomatedTests.Table1.SubsetGenerator;

public class BitSetOpsTest {
    public static void main(String[] args) {
        for (int i=0 ; i<16 ; i++){
            if (i%4 == 0 && i>0) System.out.println();
            System.out.print(SubsetGenerator.getBitsetString(i) + " ");
        }

        System.out.println();
        System.out.println(SubsetGenerator.countCustomers(14));
        System.out.println(SubsetGenerator.countVehicles(14,13));

        System.out.println(SubsetGenerator.getBitsetString(461464));
        System.out.println(SubsetGenerator.getBitsetString(832673));
        System.out.println(SubsetGenerator.countCustomers(461464));
        System.out.println(SubsetGenerator.countVehicles(461464,832673));
    }
}
