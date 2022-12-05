package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.api.AuthServerFeignClient;

@Service
public class LoginService {
    @Autowired
    private AuthServerFeignClient authServerFeignClient;

    public ResponseData<ApiToken> login(String uname, String pwd) {
        return authServerFeignClient.getToken(uname, pwd);
    }
}
