package xyz.defe.sp.test.interceptor;

import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import xyz.defe.sp.test.config.HeaderConfig;

import java.io.IOException;

@Component
public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (!Strings.isNullOrEmpty(HeaderConfig.uid)) {
            request.getHeaders().add("uid", HeaderConfig.uid);
        }
        if (!Strings.isNullOrEmpty(HeaderConfig.token)) {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, HeaderConfig.token);
        }
        return execution.execute(request, body);
    }
}
