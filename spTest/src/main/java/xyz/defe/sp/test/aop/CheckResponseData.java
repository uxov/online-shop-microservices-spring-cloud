package xyz.defe.sp.test.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.assertj.core.util.Strings;
import org.springframework.stereotype.Component;
import xyz.defe.sp.common.pojo.ResponseData;

@Aspect
@Component
public class CheckResponseData {
    @Pointcut("execution(xyz.defe.sp.common.pojo.ResponseData xyz.defe.sp.test..*.*(..))")
    public void pointcut() {}

    @AfterReturning(value = "pointcut()", returning = "returnValue")
    public void after(JoinPoint joinPoint, Object returnValue) {
        ResponseData responseData = (ResponseData) returnValue;
        if (!Strings.isNullOrEmpty(responseData.getError())) {
            System.out.println(responseData.getServiceName() + " - " + responseData.getStatus() + " - ERROR: " + responseData.getError());
        }
        if (!Strings.isNullOrEmpty(responseData.getMessage())) {
            System.out.println(responseData.getServiceName() + " - " + responseData.getStatus() +  " - INFO: " + responseData.getMessage());
        }
    }
}
