package xyz.defe.sp.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.test.interceptor.RestTemplateInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Config {
    @Autowired
    private RestTemplateInterceptor restTemplateInterceptor;

    @Bean
    public RestTemplate restTemplate(HttpMessageConverters bootConverters) {
        RestTemplate restTemplate = new RestTemplate(bootConverters.getConverters());
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors.isEmpty()) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(restTemplateInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
