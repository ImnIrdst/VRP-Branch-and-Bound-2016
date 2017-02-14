package Main.AutomatedTests;

import Main.AutomatedTests.AutoTests.CplexAutoTest;
import Main.AutomatedTests.AutoTests.GeneticAutoTest;
import Main.GlobalVars;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
//        GlobalVars.initTheLogFile("resources/log-tmp.txt");

//        CplexAutoTest.main(args);
//        Genetic1AutoTest.main(args);
//        Genetic3AutoTest.main(args);
        GeneticAutoTest.main(args);

        GlobalVars.log.close();
    }
}
