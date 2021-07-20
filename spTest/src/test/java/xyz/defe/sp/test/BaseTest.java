package xyz.defe.sp.test;

import org.assertj.core.util.Strings;
import xyz.defe.sp.common.pojo.ResponseData;

public class BaseTest {
    public ResponseData request(ThrowingRunnable func) {
        ResponseData responseData = null;
        try {
            responseData = func.run();
            if (responseData.getStatus() != 200) {
                System.out.println("ERROR: " + responseData.getError());
            } else if (!Strings.isNullOrEmpty(responseData.getMessage())) {
                System.out.println("INFO: " + responseData.getMessage());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return responseData;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        ResponseData run() throws Throwable;
    }
}
