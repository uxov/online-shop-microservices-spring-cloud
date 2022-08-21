package xyz.defe.sp.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.web.api.UserServiceFeignClient;

@Service
public class LoginService {
    @Autowired
    private UserServiceFeignClient userServiceFeignClient;

    public Account login(String uname, String pwd) {
        return userServiceFeignClient.verify(uname, pwd).getData();
    }
}
