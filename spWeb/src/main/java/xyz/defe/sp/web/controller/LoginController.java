package xyz.defe.sp.web.controller;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.response.ResponseDataResult;
import xyz.defe.sp.web.Constant;
import xyz.defe.sp.web.service.LoginService;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@ResponseDataResult
public class LoginController {
    @Autowired
    public RedissonClient redisson;
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Object login(@RequestParam String uname, @RequestParam String pwd) {
        ResponseData<ApiToken> responseData = loginService.login(uname, pwd);
        ApiToken apiToken = responseData.getData();
        if (apiToken == null) {return responseData;}

        redisson.getBucket(Constant.TOKEN_PREFIX + apiToken.getToken())
                .set(apiToken.getUid(), Duration.ofHours(1));

        Map result = new HashMap();
        result.put("uid", apiToken.getUid());
        result.put("token", apiToken.getToken());
        return result;
    }
}
