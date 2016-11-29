package Main.AutomatedTests;

import Main.AutomatedTests.Experiment2.*;
import Main.GlobalVars;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        GlobalVars.initTheLogFile("resources/Experiments/Ex2/log-tmp.txt");

        BSAutoTest.main(args);
        Genetic1AutoTest.main(args);
//        BBATCAutoTest.main(args);
        Genetic3AutoTest.main(args);
        Genetic4AutoTest.main(args);
//        CplexAutoTest.main(args);
//        BBGAAutoTest.main(args);

        GlobalVars.log.close();
    }
}
