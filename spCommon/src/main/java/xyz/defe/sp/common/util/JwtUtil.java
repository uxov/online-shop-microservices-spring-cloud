package xyz.defe.sp.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    private static String ISUSER = "admin";

    public static String generateToken(String userId, String userName, String secret, long millis) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + millis);
        String token = JWT.create()
                .withIssuer(ISUSER)
                .withIssuedAt(now)
                .withExpiresAt(expireTime)
                .withClaim("uid", userId)
                .withClaim("username", userName)
                .sign(algorithm);
        return token;
    }

    public static void verifyToken(String token, String secretKey) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier jwtVerifier = JWT.require(algorithm).acceptLeeway(2).withIssuer(ISUSER).build();
        jwtVerifier.verify(token);
    }

    public static String getUserId(String token, String secret) throws Exception {
        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim("uid").asString();
    }

    public static String getUserName(String token, String secret) throws Exception {
        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim("username").asString();
    }
}
