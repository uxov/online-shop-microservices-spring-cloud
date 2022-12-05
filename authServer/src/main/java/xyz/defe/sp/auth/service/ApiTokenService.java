package xyz.defe.sp.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.util.JwtUtil;

@Service
public class ApiTokenService {
    @Value("${token.secretKey}")
    private String secretKey;
    @Value("${token.expiredMillis}")
    private long expiredMillis;

    @Autowired
    private UserFeignClient userFeignClient;

    public ApiToken generateToken(String uname, String pwd) {
        Account account = userFeignClient.verify(uname, pwd).getData();
        ApiToken apiToken = new ApiToken();
        if (account != null) {
            String token = JwtUtil.generateToken(account.getId(), account.getUname(), secretKey, expiredMillis);
            apiToken.setUid(account.getId());
            apiToken.setToken(token);
        }
        return apiToken;
    }


}
