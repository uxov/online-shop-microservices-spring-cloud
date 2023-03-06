package xyz.defe.sp.test.restTemplate.services.spUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.test.Users;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SpUserTest {
    @Autowired
    private SpUserRequest spUserRequest;

    @Test
    void verify() {
        Account account = spUserRequest.verify(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertEquals(Users.MIKE.uname, account.getUname());
    }

}
