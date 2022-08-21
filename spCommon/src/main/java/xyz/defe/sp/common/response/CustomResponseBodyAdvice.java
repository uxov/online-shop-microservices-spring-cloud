package xyz.defe.sp.common.response;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import xyz.defe.sp.common.pojo.ResponseData;

@ControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    @Autowired
    private Gson gson;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getDeclaringClass().isAnnotationPresent(ResponseDataResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                        Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                        ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ResponseData) {
            return body;
        } else {
            ResponseData responseData = new ResponseData();
            responseData.setData(body);
            responseData.setStatus(HttpStatus.OK.value());
//            if (body instanceof String) {return gson.toJson(responseData);}
            return responseData;
        }
    }

}
