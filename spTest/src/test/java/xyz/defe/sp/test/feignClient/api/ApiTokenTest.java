package xyz.defe.sp.test.feignClient.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.defe.sp.common.entity.spPayment.Wallet;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.test.BaseTest;
import xyz.defe.sp.test.config.TokenConfig;

@SpringBootTest
public class ApiTokenTest extends BaseTest {
    @Autowired
    private Api api;

    @Test
    void getToken() {
        ApiToken apiToken = (ApiToken) request(
                () -> api.getToken("mike", "123")
            ).getData();
        Assertions.assertNotNull(apiToken);
        System.out.println("api token = " + apiToken.getToken());
    }

    @Test
    void getWallet() {
        // get wallet with token
        System.out.println("get wallet with token");
        TokenConfig.token = "";
        ApiToken apiToken = api.getToken("mike", "123").getData();
        Assertions.assertNotNull(apiToken);
        TokenConfig.token = apiToken.getToken();
        String uid = apiToken.getUid();
        Wallet wallet = (Wallet) request(() -> api.getWallet(uid)).getData();
        Assertions.assertNotNull(wallet);
        System.out.println("balance = " + wallet.getBalance());

        System.out.println();

        // get wallet without token
        System.out.println("get wallet without token");
        TokenConfig.token = "";
        Wallet wallet1 = (Wallet) request(() -> api.getWallet(uid)).getData();
        Assertions.assertNull(wallet1);
    }
}
