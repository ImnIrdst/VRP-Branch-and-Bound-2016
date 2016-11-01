package Main.AutomatedTests;

import Main.AutomatedTests.Experiment2.*;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        CplexAutoTest.main(args);
        BBGAAutoTest.main(args);
        BBATCAutoTest.main(args);
        BSAutoTest.main(args);
        Genetic1AutoTest.main(args);
        Genetic3AutoTest.main(args);
        Genetic4AutoTest.main(args);
    }
}
