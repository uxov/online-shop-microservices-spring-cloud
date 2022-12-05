package xyz.defe.sp.web.interceptor;

import com.google.common.base.Strings;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        //RequestContextHolder 可以获得当前线程绑定的 Request 对象
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = sra.getRequest();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!Strings.isNullOrEmpty(token)) {
            template.header(HttpHeaders.AUTHORIZATION, token);
        }
    }
}