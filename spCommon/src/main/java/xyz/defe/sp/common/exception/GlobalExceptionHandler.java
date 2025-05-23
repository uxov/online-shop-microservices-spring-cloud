package xyz.defe.sp.common.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.defe.sp.common.pojo.ResponseData;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @Value("${spring.application.name}")
    private String serviceName;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public ResponseData defaultExceptionHandler(HttpServletRequest req, Exception e) {
        ResponseData response = new ResponseData();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(e.getMessage());
        response.setServiceName(serviceName);
        log.error(e.getMessage(), e);
        return response;
    }

    @ExceptionHandler(FeignException.class)
    public ResponseData handleFeignException(FeignException e) {
        ResponseData response = new ResponseData();
        response.setStatus(e.status());
        response.setError(e.getMessage());
        response.setServiceName(serviceName);
        log.error(e.getMessage(), e);
        return response;
    }

    @ExceptionHandler({WarnException.class, ServletRequestBindingException.class})
    public ResponseData handleRequestBinding(Exception e) {
        ResponseData response = new ResponseData();
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(e.getMessage());
        response.setServiceName(serviceName);
        log.error(e.getMessage(), e);
        return response;
    }
}
