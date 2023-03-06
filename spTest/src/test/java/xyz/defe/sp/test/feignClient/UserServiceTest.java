package xyz.defe.sp.test.feignClient;

import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.test.Users;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void verify(){
        Account account = userService.verify(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertEquals(Users.MIKE.name, account.getName());
    }

}
