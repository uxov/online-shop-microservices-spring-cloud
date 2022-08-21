package xyz.defe.sp.test.interceptor;

import com.google.common.base.Strings;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import xyz.defe.sp.test.config.TokenConfig;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        if (!Strings.isNullOrEmpty(TokenConfig.token)) {
            template.header(HttpHeaders.AUTHORIZATION, TokenConfig.token);
        }
    }
}
