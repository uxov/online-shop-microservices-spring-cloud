package xyz.defe.sp.common.exception;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import xyz.defe.sp.common.pojo.ResponseData;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(-2)
@Component
public class FilterExceptionHandler extends OncePerRequestFilter {
    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private Gson gson;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            e.printStackTrace();

            ResponseData responseData = new ResponseData();
            responseData.setStatus(HttpStatus.BAD_REQUEST.value());
            if (e instanceof WarnException) {
                responseData.setMessage(e.getMessage());
            } else {
                responseData.setError(e.getMessage());
            }
            responseData.setServiceName(serviceName);

            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(gson.toJson(responseData));
        }
    }
}
