package xyz.defe.sp.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.defe.sp.common.pojo.ResponseData;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = Exception.class)
    public ResponseData defaultExceptionHandler(HttpServletRequest req, Exception e) {
        ResponseData response = new ResponseData();
        if (e instanceof WarnException) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(e.getMessage());
            log.warn(e.getMessage());
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError(e.getMessage());
            log.error(e.getMessage(), e);
        }
        return response;
    }

}
