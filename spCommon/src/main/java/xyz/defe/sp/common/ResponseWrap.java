package xyz.defe.sp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import xyz.defe.sp.common.pojo.ResponseData;

@Component
public class ResponseWrap {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ResponseData wrap(ThrowingRunnable func) {
        ResponseData responseData = new ResponseData();
        try {
            Object data = func.run();
            if (data instanceof ResponseData) {
                responseData = (ResponseData) data;
            } else {
                responseData.setData(data);
            }
        } catch (WarnException we) {
            responseData.setStatus(500);
            responseData.setError(we.getMessage());
            log.warn(we.getMessage());
        } catch (Throwable e) {
            responseData.setStatus(500);
            responseData.setError(e.getMessage());
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return responseData;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        Object run() throws Throwable;
    }
}
