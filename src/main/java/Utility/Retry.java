package Utility;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private final int maxRetryCnt = 1;
    private int retryCnt = 0;

    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (retryCnt < maxRetryCnt) {
                System.out.println("Retrying " + result.getName() + " again and the count is " + (retryCnt + 1));
                retryCnt++;
                return true;
            }
        }
        return false;
    }
}