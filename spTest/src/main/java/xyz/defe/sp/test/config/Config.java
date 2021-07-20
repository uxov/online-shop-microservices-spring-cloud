package xyz.defe.sp.test.services.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    public RestTemplate restTemplate(HttpMessageConverters bootConverters) {
        RestTemplate restTemplate = new RestTemplate(bootConverters.getConverters());
        return restTemplate;
    }
}
