package Main.AutomatedTests;

import Main.AutomatedTests.Experiment2.BBAutoTest;
import Main.AutomatedTests.Experiment2.CplexAutoTest;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        BBAutoTest.main(args);
        CplexAutoTest.main(args);

    }
}
