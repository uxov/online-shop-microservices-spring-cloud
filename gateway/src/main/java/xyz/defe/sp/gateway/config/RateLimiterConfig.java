package xyz.defe.sp.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    KeyResolver pathKeyResolver() {
        return exchange
                -> Mono.just(exchange.getRequest().getPath().value());
    }

    @Bean
    public KeyResolver serviceNameKeyResolver() {
        return exchange
                -> Mono.just(exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR));
    }

    @Bean
    @Primary
    public KeyResolver globalKeyResolver() {
        return exchange -> Mono.just("global_limit_key");
    }
}
