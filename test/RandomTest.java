import Main.Algorithms.Other.Random;


public class RandomTest {
    public static void main(String[] args){
        Random.setSeed(1);
        for (int i=0 ; i<30 ; i++){
            System.out.print(Random.getRandomIntInRange(new Random.IRange(2,3)) + " ");
        }
        System.out.println();

        for (int i=0 ; i<30 ; i++){
            System.out.printf("%.2f ", Random.getRandomDoubleInRange(new Random.DRange(0, 1)));
        }
        System.out.println();
    }
}
