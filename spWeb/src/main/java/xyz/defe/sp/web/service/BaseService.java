package xyz.defe.sp.web.service;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.pojo.ResponseData;

public class BaseService {
    @Autowired
    public RestTemplate rest;
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${gateway.url}")
    public String baseURL;

    public ResponseData request(ThrowingRunnable func) throws Exception {
        ResponseData responseData = null;
        try {
            responseData = func.run();
        } catch (Throwable throwable) {
            throw new Exception(throwable.getMessage());
        }
        if (responseData.getStatus() != 200 && !Strings.isNullOrEmpty(responseData.getError())) {
            throw new Exception(responseData.getError());
        }
        return responseData;
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        ResponseData run() throws Throwable;
    }
}
