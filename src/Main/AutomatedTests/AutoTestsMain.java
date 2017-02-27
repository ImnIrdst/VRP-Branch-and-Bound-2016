package Main.AutomatedTests;

//import Main.AutomatedTests.AutoTests.CplexAutoTest;
import Main.AutomatedTests.AutoTests.HeuristicGeneticAlgorithmTest;
import Main.AutomatedTests.AutoTests.SimpleGeneticAlgorithmTest;
import Main.GlobalVars;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        GlobalVars.initTheLogFile("resources/log-tmp.txt");

//        CplexAutoTest.main(args);
        SimpleGeneticAlgorithmTest.main(args);
        HeuristicGeneticAlgorithmTest.main(args);

        GlobalVars.log.close();
    }
}
