package xyz.defe.sp.web.test.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.web.service.LoginService;

@SpringBootTest
public class LoginServiceTest {
    @Autowired
    private LoginService loginService;

    @Test
    void login() throws Exception {
        Account account = loginService.login("mike", "123");
        Assertions.assertEquals("Mike", account.getName());
    }
}
