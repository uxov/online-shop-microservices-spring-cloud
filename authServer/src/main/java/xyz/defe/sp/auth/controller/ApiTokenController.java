package xyz.defe.sp.auth.controller;

import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.auth.service.ApiTokenService;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;

@RestController
@ResponseDataResult
@RequestMapping("api/token")
public class ApiTokenController {
    @Autowired
    private ApiTokenService apiTokenService;

    @PostMapping
    public Object getToken(String uname, String pwd) {
        ApiToken apiToken = apiTokenService.generateToken(uname, pwd);
        ResponseData responseData = new ResponseData();
        if (Strings.isNullOrEmpty(apiToken.getToken())) {
            responseData.setStatus(HttpStatus.BAD_REQUEST.value())
                    .setMessage("Invalid user name or password!");
        } else {
            responseData.setData(apiToken);
        }
        return responseData;
    }

}
