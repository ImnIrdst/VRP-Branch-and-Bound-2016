package Main.AutomatedTests;

import Main.AutomatedTests.AutoTests.CplexAutoTest;
import Main.AutomatedTests.AutoTests.Genetic1AutoTest;
import Main.AutomatedTests.AutoTests.Genetic3AutoTest;
import Main.AutomatedTests.AutoTests.Genetic4AutoTest;
import Main.GlobalVars;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        GlobalVars.initTheLogFile("resources/log-tmp.txt");

//        CplexAutoTest.main(args);
//        Genetic1AutoTest.main(args);
//        Genetic3AutoTest.main(args);
        Genetic4AutoTest.main(args);

        GlobalVars.log.close();
    }
}
