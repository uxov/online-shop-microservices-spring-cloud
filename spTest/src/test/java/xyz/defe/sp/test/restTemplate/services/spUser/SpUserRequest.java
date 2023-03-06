package xyz.defe.sp.test.restTemplate.services.spUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.rest.RestUtil;

import java.util.List;
import java.util.Map;

@Component
public class SpUserRequest {
    @Autowired
    private RestTemplate rest;
    private static String baseURL = "http://localhost:9200/userService/";

    public ResponseData<Account> verify(String uname, String pwd) {
        Map<String, String> paramMap = Map.of("uname", uname, "pwd", pwd);
        return RestUtil.INSTANCE.set(rest)
                .post(baseURL + "account/verify", paramMap, new ParameterizedTypeReference<ResponseData<Account>>() {});
    }

    public ResponseData createAccount(List<Account> accounts) {
        return RestUtil.INSTANCE.set(rest).post(baseURL + "accounts", accounts);
    }
}
