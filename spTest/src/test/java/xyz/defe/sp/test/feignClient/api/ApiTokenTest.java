package xyz.defe.sp.test.feignClient.api;

import org.assertj.core.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.test.Users;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApiTokenTest {
    @Autowired
    private Api api;

    @Test
    void getToken() {
        ApiToken apiToken = api.getToken(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertTrue(!Strings.isNullOrEmpty(apiToken.getToken()));
    }

    @Test
    void getWallet() {
        // get wallet with token
        System.out.println("get wallet with token");

        ApiToken apiToken = api.getToken(Users.MIKE.uname, Users.MIKE.pwd).getData();
        assertTrue(!Strings.isNullOrEmpty(apiToken.getToken()));
        Wallet wallet = api.getWallet(apiToken.getUid(), apiToken.getToken()).getData();
        assertNotNull(wallet.getBalance());

        System.out.println("balance = " + wallet.getBalance());
        System.out.println();

        // get wallet without token
        System.out.println("get wallet without token");
        Wallet wallet2 = api.getWallet(apiToken.getUid(), null).getData();
        assertNull(wallet2);
    }
}
