package xyz.defe.sp.auth.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import xyz.defe.sp.auth.service.ApiTokenService;
import xyz.defe.sp.auth.service.UserFeignClient;
import xyz.defe.sp.common.entity.spUser.Account;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.exception.WarnException;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.common.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiTokenServiceTest {
    @Mock
    private UserFeignClient userFeignClient;

    @InjectMocks
    private ApiTokenService apiTokenService;

    private final String SECRET_KEY = "testSecretKey";
    private final long EXPIRED_MILLIS = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(apiTokenService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(apiTokenService, "expiredMillis", EXPIRED_MILLIS);
    }

    @Test
    void generateTokenSuccess() throws Exception {
        Account mockAccount = new Account();
        mockAccount.setId("user123");
        mockAccount.setUname("testUser");
        ResponseData<Account> successResponse = new ResponseData<Account>().setData(mockAccount);

        when(userFeignClient.verify(anyString(), anyString())).thenReturn(successResponse);

        ApiToken result = apiTokenService.generateToken("testUser", "password123");

        assertNotNull(result);
        assertEquals(mockAccount.getId(), result.getUid());
        assertNotNull(result.getToken());

        assertEquals(mockAccount.getId(), JwtUtil.getUserId(result.getToken(), SECRET_KEY));
        assertEquals(mockAccount.getUname(), JwtUtil.getUserName(result.getToken(), SECRET_KEY));
    }

    @Test
    void generateTokenFailure() {
        ResponseData<Account> failureResponse = new ResponseData<Account>()
            .setData(null)
            .setMessage("Invalid credentials");

        when(userFeignClient.verify(anyString(), anyString())).thenReturn(failureResponse);

        assertThrows(WarnException.class, () ->
                apiTokenService.generateToken("wrongUser", "wrongPass")
        );

    }
}
