package Main.AutomatedTests;

import Main.AutomatedTests.SCSTests.SCSTestGenerator;
import Main.MathematicalModel.Model;

/**
 * Run a test set
 */
public class AutoTestsMain2 {
    public static void main(String[] args) throws Exception {
        SCSTestGenerator testGenerator = new SCSTestGenerator();
        testGenerator.addSmallTestsV1();

        for (int testId = 0; testGenerator.hasNextTestCase(); testId++) {
            Model.main2(testGenerator.getNextTestCase(), testId);
        }
    }
}
