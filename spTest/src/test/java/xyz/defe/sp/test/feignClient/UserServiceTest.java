package xyz.defe.sp.test.feignClient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    void verify(){
        Account account = userService.verify("mike", "123").getData();
        assertEquals("Mike", account.getName());
    }

    @Test
    void addAccount() throws Exception {
        Account account = new Account();
        account.setName("Mike");
        account.setUname("mike");
        account.setAge(25);
        account.setPwd("123");
        ResponseData responseData  = userService.createAccounts(List.of(account));
        if (responseData.getStatus() != 200) {
            throw new Exception(responseData.getError());
        }
    }
}
