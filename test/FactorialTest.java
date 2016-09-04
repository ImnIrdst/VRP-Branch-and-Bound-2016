import Main.Algorithms.Other.Factorial;

/**
 * Created by iman on 7/27/16.
 */
public class FactorialTest {
    public static void main(String[] args){
        for (int i=0 ; i<Factorial.getFactSize() ; i++){
            System.out.println(i + ": " + Factorial.getFactorial(i));
        }
    }
}
