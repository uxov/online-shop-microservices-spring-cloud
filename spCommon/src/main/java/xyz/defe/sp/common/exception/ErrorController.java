package xyz.defe.sp.common.exception;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;

import java.util.Map;

@RestController
@ResponseDataResult
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
    private final ErrorAttributes errorAttributes;

    public ErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(("${server.error.path:/error}"))
    public ResponseData notFound(WebRequest webRequest, HttpServletRequest request) {
        ResponseData responseData = new ResponseData();
        Map<String, Object> eaMap = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        responseData.setStatus(Integer.parseInt(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString()));
        responseData.setError(eaMap.get("error").toString());
        responseData.setMessage("Request Path = " + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        return responseData;
    }
}
