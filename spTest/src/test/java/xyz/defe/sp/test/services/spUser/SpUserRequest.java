package xyz.defe.sp.test.services.spUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;
import xyz.defe.sp.test.BaseTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpUserRequest extends BaseTest {
    @Autowired
    private RestTemplate rest;
    private static String baseURL = "http://localhost:9002/userService/";

    public Account verify(String uname, String pwd) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("uname", uname);
        paramMap.put("pwd", pwd);
        ResponseData<Account> responseData = request(() -> {
            return RestUtil.INSTANCE.set(rest)
                    .post(baseURL + "verify", paramMap, new ParameterizedTypeReference<ResponseData<Account>>() {});
        });
        return responseData.getData();
    }

    public ResponseData createAccount(List<Account> accounts) {
        return request(() -> {
            return RestUtil.INSTANCE.set(rest).post(baseURL, accounts);
        });
    }
}
