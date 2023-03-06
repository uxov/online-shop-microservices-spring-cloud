package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.Constant;
import xyz.defe.sp.web.service.LoginService;

import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseDataResult
public class LoginController {
    @Autowired
    private Cache cache;
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Object login(String uname, String pwd) {
        ResponseData<ApiToken> responseData = loginService.login(uname, pwd);
        ApiToken apiToken = responseData.getData();
        if (apiToken == null) {
            return responseData;
        }
        Map result = new HashMap();
        cache.put(Constant.TOKEN_PREFIX + apiToken.getToken(), apiToken.getUid());
        cache.put(Constant.UID_PREFIX + apiToken.getUid(), apiToken.getToken());
        result.put("uid", apiToken.getUid());
        result.put("token", apiToken.getToken());
        return result;
    }
}
