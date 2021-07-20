package xyz.defe.sp.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.defe.sp.common.Cache;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.Constant;
import xyz.defe.sp.web.service.LoginService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class LoginController extends BaseController {
    @Autowired
    private Cache cache;
    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseData login(String uname, String pwd) {
        return response(() -> {
            String token = null;
            Map result = new HashMap();
            Account account = loginService.login(uname, pwd);
            if (null != account) {
                token = UUID.randomUUID().toString();
                cache.put(Constant.TOKEN_PREFIX + token, account.getId());
                cache.put(Constant.UID_PREFIX + account.getId(), account);
                result.put("uid", account.getId());
                result.put("token", token);
            }
            return result;
        });
    }
}
