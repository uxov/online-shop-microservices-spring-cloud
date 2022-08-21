package xyz.defe.sp.test.restTemplate.services.spUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;

@SpringBootTest
public class SpUserTest {
    @Autowired
    private SpUserRequest spUserRequest;

    @Test
    void verify() {
        Account account = spUserRequest.verify("mike", "123");
        Assertions.assertNotNull(account);
        Assertions.assertEquals("mike", account.getUname());
    }

}
