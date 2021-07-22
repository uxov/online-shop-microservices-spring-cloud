package xyz.defe.sp.web.service;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.exception.WarnException;
import xyz.defe.sp.common.pojo.ResponseData;

public class BaseService {
    @Autowired
    public RestTemplate rest;

    @Value("${gateway.url}")
    public String baseURL;

    public ResponseData request(Runnable func) {
        ResponseData responseData = func.run();
        if (responseData.getStatus() != HttpStatus.OK.value()) {
            if (!Strings.isNullOrEmpty(responseData.getError())) {
                throw new RuntimeException(responseData.getError());
            }
            if (!Strings.isNullOrEmpty(responseData.getMessage())) {
                throw new WarnException(responseData.getMessage());
            }
        }
        return responseData;
    }

    @FunctionalInterface
    public interface Runnable {
        ResponseData run();
    }
}
