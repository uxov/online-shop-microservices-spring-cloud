package xyz.defe.sp.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    KeyResolver pahtKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
