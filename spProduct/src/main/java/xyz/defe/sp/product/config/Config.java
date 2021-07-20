package xyz.defe.sp.product.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(HttpMessageConverters bootConverters) {
        RestTemplate restTemplate = new RestTemplate(bootConverters.getConverters());
        return restTemplate;
    }
}
