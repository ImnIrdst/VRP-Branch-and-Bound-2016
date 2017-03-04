package Main.AutomatedTests;

//import Main.AutomatedTests.AutoTests.CplexAutoTest;
import Main.Algorithms.Heuristics.TabuSearch.TabuSearch;
import Main.AutomatedTests.AutoTests.HeuristicGeneticAlgorithmTest;
import Main.AutomatedTests.AutoTests.SimpleGeneticAlgorithmTest;
import Main.AutomatedTests.AutoTests.TabuSearchTest;
import Main.GlobalVars;

/**
 * Run a test set
 */
public class AutoTestsMain {
    public static void main(String[] args) throws Exception {
        GlobalVars.initTheLogFile("resources/log-tmp.txt");

//        CplexAutoTest.main(args);
        HeuristicGeneticAlgorithmTest.main(args);
        TabuSearchTest.main(args);
        SimpleGeneticAlgorithmTest.main(args);

        GlobalVars.log.close();
    }
}
