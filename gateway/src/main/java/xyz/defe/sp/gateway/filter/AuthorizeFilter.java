package xyz.defe.sp.gateway.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.util.JwtUtil;

import java.util.List;

@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Value("${token.secretKey}")
    private String secretKey;
    @Value("${allowPath}")
    private List<String> allowPath;
    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private Gson gson;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().toString();
        log.debug(request.getMethod().name() + " - {}", path);
        if (!startWithAllowPath(path, allowPath)) {
            String token = request.getHeaders().getFirst("Authorization");
            log.debug("token = {}", token);
            if (Strings.isNullOrEmpty(token)) {
                return getVoidMono(response, HttpStatus.UNAUTHORIZED.value(), "Token is null or empty!");
            }
            try {
                JwtUtil.verifyToken(token, secretKey);
                String uid = JwtUtil.getUserId(token, secretKey);
                String uname = JwtUtil.getUserName(token, secretKey);
                ServerHttpRequest rebuildRequest = request.mutate()
                        .header("uid", uid)
                        .header("uname", uname).build();
                return chain.filter(exchange.mutate().request(rebuildRequest).build());
            } catch (Exception e) {
                e.printStackTrace();
                return getVoidMono(response, HttpStatus.UNAUTHORIZED.value(), e.getMessage());
            }
        }
        return chain.filter(exchange);
    }

    private boolean startWithAllowPath(String path, List<String> allowPath) {
        for (String s : allowPath) {
            if (path.startsWith(s)) {return true;}
        }
        return false;
    }

    private Mono<Void> getVoidMono(ServerHttpResponse serverHttpResponse, int stateCode, String message) {
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        ResponseData responseData = new ResponseData();
        responseData.setStatus(stateCode);
        responseData.setMessage(message);
        responseData.setServiceName(serviceName);
        DataBuffer dataBuffer = serverHttpResponse.bufferFactory().wrap(gson.toJson(responseData).getBytes());
        return serverHttpResponse.writeWith(Flux.just(dataBuffer));
    }

    @Override
    public int getOrder() {
        return -10;
    }
}
