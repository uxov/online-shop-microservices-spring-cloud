package xyz.defe.sp.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.auth.service.ApiTokenService;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.response.ResponseDataResult;

@RestController
@ResponseDataResult
@RequestMapping("api/token")
public class ApiTokenController {
    @Autowired
    private ApiTokenService apiTokenService;

    @PostMapping
    public ApiToken getToken(String uname, String pwd) {
        ApiToken apiToken = apiTokenService.generateToken(uname, pwd);
        return apiToken;
    }

}
