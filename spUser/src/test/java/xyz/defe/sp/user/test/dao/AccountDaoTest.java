package xyz.defe.sp.user.test.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.user.dao.AccountDao;

@SpringBootTest
public class AccountDaoTest {
    @Autowired
    private AccountDao accountDao;

    @Test
    public void verify() {
        Account account = accountDao.findByUnameAndPwd("mike", "123");
        Assertions.assertNotNull(account);
        Assertions.assertEquals("Mike", account.getName());
    }
}
