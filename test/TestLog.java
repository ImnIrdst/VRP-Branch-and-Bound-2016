/**
 * Created by PRJ12 on 8/24/2016.
 */
public class TestLog {
    public static void main(String[] args) {
        for (double theta = 0.1; theta < 1.0; theta += 0.1) {
            System.out.printf("t:%.2f, log: %.2f\n", theta, Math.log10(2 + 10 * theta));
        }
        for (double theta = 0.1; theta < 1.0; theta += 0.1) {
            System.out.printf("t:%.2f, exp: %.2f\n", theta, Math.exp(theta*50/8.));
        }
    }
}
