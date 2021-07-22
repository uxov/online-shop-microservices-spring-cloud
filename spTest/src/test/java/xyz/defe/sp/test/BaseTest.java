package xyz.defe.sp.test;

import org.assertj.core.util.Strings;
import xyz.defe.sp.common.pojo.ResponseData;

public class BaseTest {
    public ResponseData request(Runnable func) {
        ResponseData responseData = func.run();
        if (!Strings.isNullOrEmpty(responseData.getError())) {
            System.out.println("ERROR: " + responseData.getError());
        }
        if (!Strings.isNullOrEmpty(responseData.getMessage())) {
            System.out.println("INFO: " + responseData.getMessage());
        }
        return responseData;
    }

    @FunctionalInterface
    public interface Runnable {
        ResponseData run();
    }
}
