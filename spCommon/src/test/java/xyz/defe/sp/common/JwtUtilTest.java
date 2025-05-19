package xyz.defe.sp.common;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import xyz.defe.sp.common.util.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @Test
    void generateTokenReturnsValidToken() {
        String userId = "123";
        String userName = "testUser";
        String secret = "secretKey";
        long millis = 3600000;

        String token = JwtUtil.generateToken(userId, userName, secret, millis);

        assertNotNull(token);
        DecodedJWT decodedJWT = JWT.decode(token);
        assertEquals(userId, decodedJWT.getClaim("uid").asString());
        assertEquals(userName, decodedJWT.getClaim("username").asString());
        assertEquals("admin", decodedJWT.getIssuer());
    }

    @Test
    void verifyTokenDoesNotThrowExceptionForValidToken() {
        String secret = "secretKey";
        String token = JwtUtil.generateToken("123", "testUser", secret, 3600000);

        assertDoesNotThrow(() -> JwtUtil.verifyToken(token, secret));
    }

    @Test
    void verifyTokenThrowsExceptionForInvalidToken() {
        String secret = "secretKey";
        String invalidToken = "invalidToken";

        assertThrows(Exception.class, () -> JwtUtil.verifyToken(invalidToken, secret));
    }

    @Test
    void getUserIdReturnsCorrectUserId() throws Exception {
        String userId = "123";
        String secret = "secretKey";
        String token = JwtUtil.generateToken(userId, "testUser", secret, 3600000);

        String result = JwtUtil.getUserId(token, secret);

        assertEquals(userId, result);
    }

    @Test
    void getUserNameReturnsCorrectUserName() throws Exception {
        String userName = "testUser";
        String secret = "secretKey";
        String token = JwtUtil.generateToken("123", userName, secret, 3600000);

        String result = JwtUtil.getUserName(token, secret);

        assertEquals(userName, result);
    }

    @Test
    void getUserIdThrowsExceptionForInvalidToken() {
        String invalidToken = "invalidToken";
        String secret = "secretKey";

        assertThrows(Exception.class, () -> JwtUtil.getUserId(invalidToken, secret));
    }

    @Test
    void getUserNameThrowsExceptionForInvalidToken() {
        String invalidToken = "invalidToken";
        String secret = "secretKey";

        assertThrows(Exception.class, () -> JwtUtil.getUserName(invalidToken, secret));
    }
}