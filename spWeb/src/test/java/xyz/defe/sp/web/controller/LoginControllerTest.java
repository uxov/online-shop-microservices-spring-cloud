package xyz.defe.sp.web.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import xyz.defe.sp.common.entity.spUser.ApiToken;
import xyz.defe.sp.common.pojo.ResponseData;
import xyz.defe.sp.web.Constant;
import xyz.defe.sp.web.service.LoginService;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private RedissonClient redisson;

    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController;

    @Test
    void loginReturnsTokenAndUidWhenCredentialsAreValid() {
        String uname = "validUser";
        String pwd = "validPassword";
        ApiToken apiToken = new ApiToken();
        apiToken.setUid("123");
        apiToken.setToken("token123");
        ResponseData<ApiToken> responseData = new ResponseData<>();
        responseData.setData(apiToken);
        RBucket<Object> bucket = mock(RBucket.class);

        when(loginService.login(uname, pwd)).thenReturn(responseData);
        when(redisson.getBucket(Constant.TOKEN_PREFIX + apiToken.getToken())).thenReturn(bucket);

        Object result = loginController.login(uname, pwd);

        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> resultMap = (Map<?, ?>) result;
        assertEquals("123", resultMap.get("uid"));
        assertEquals("token123", resultMap.get("token"));
        verify(bucket).set("123", Duration.ofHours(1));
    }

    @Test
    void loginReturnsResponseDataWhenCredentialsAreInvalid() {
        String uname = "invalidUser";
        String pwd = "invalidPassword";
        ResponseData<ApiToken> responseData = new ResponseData<>();
        responseData.setData(null);

        when(loginService.login(uname, pwd)).thenReturn(responseData);

        Object result = loginController.login(uname, pwd);

        assertNotNull(result);
        assertTrue(result instanceof ResponseData);
        assertNull(((ResponseData<?>) result).getData());
    }
}