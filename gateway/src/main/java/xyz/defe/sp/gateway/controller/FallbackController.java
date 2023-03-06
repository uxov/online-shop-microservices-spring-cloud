package xyz.defe.sp.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import xyz.defe.sp.common.pojo.ResponseData;

@RestController
public class FallbackController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("defaultFallback")
    public ResponseData defaultFallback(ServerWebExchange exchange) {
        Exception exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        ServerWebExchange delegate = ((ServerWebExchangeDecorator) exchange).getDelegate();
        log.error("request failed: {}", delegate.getRequest().getURI().getPath(), exception);

        ResponseData responseData = new ResponseData();
        responseData.setServiceName("Gateway");
        responseData.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        responseData.setError(exception.getMessage());
        responseData.setMessage("request failed");
        return responseData;
    }

    @RequestMapping("productServiceFallback")
    public ResponseData productServiceFallback(ServerWebExchange exchange) {
        Exception exception = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        ServerWebExchange delegate = ((ServerWebExchangeDecorator) exchange).getDelegate();
        log.error("request to PRODUCT SERVICE failed: {}", delegate.getRequest().getURI().getPath(), exception);

        ResponseData responseData = new ResponseData();
        responseData.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        responseData.setError(exception.getMessage());
        responseData.setMessage("request to PRODUCT SERVICE failed");
        return responseData;
    }
}
