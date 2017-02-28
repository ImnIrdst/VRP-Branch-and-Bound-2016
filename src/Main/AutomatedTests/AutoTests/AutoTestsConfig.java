package Main.AutomatedTests.AutoTests;

/**
 * Created by iman on 2/28/17.
 */
public class AutoTestsConfig {
    public static boolean stopCriteria(int testId) {
        if (testId >= 3)
            return true;
        return false;
    }
}
