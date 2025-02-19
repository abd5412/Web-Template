package com.abd.server.utils;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtUtil {

    // 密钥，应该保存在安全的地方，而不是硬编码在代码中
    private static final String SECRET_KEY = "123asdfghjkbvxczzsfre4567890qwertyuiopasdfghjklzxcvbnm";

    // 令牌过期时间（以秒为单位），可以根据需要调整
    private static final long EXPIRATION_TIME = 1000 * 60 * 60; // 1小时

    /**
     * 生成JWT令牌
     *
     * @param userId 用户ID
     * @return 生成的JWT令牌
     */
    public static String generateToken(String userId) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);

        return Jwts.builder()
//                .setClaims(claims)
                .setSubject(userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    /**
     * 验证JWT令牌
     *
     * @param token 要验证的JWT令牌
     * @return 验证成功返回claims，验证失败抛出异常
     */
    public static Claims verifyToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从JWT令牌中获取用户ID
     *
     * @param token JWT令牌
     * @return 用户ID
     */
    public static String getUserIdFromToken(String token) {
        Claims claims = verifyToken(token);
        return (String) claims.get("userId");
    }

    /**
     * 判断JWT令牌是否过期
     *
     * @param token JWT令牌
     * @return 令牌未过期返回true，过期返回false
     */
    public static boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 从JWT令牌中获取过期时间
     *
     * @param token JWT令牌
     * @return 过期时间
     */
    public static Date getExpirationDateFromToken(String token) {
        Claims claims = verifyToken(token);
        return claims.getExpiration();
    }


}
