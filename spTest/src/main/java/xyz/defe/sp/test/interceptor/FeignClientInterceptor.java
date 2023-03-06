package xyz.defe.sp.test.interceptor;

import com.google.common.base.Strings;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import xyz.defe.sp.test.config.HeaderConfig;

//@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        if (!Strings.isNullOrEmpty(HeaderConfig.token)) {
            template.header(HttpHeaders.AUTHORIZATION, HeaderConfig.token);
        }
    }
}
