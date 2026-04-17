package com.hnkjzy.ecg_collection.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hnkjzy.ecg_collection.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * JWT 工具类（基于 JJWT）。
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("jwt.secret-key 长度不足，HS256 至少需要 32 字节");
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 Token（仅 subject）。
     */
    public String generateToken(String subject) {
        return generateToken(subject, Collections.emptyMap());
    }

    /**
     * 生成 Token（自定义 claims + subject）。
     */
    public String generateToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expireAt = DateUtil.offsetMinute(now, jwtProperties.getExpirationMinutes().intValue());

        return Jwts.builder()
                .claims(claims == null ? Collections.emptyMap() : claims)
                .subject(subject)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析 Token，返回 claims。
     */
    public Claims parseToken(String token) {
        String normalizedToken = normalizeToken(token);
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(normalizedToken)
                .getPayload();
    }

    /**
     * 校验 Token：可解析且未过期。
     */
    public boolean validateToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration() != null && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * 获取 subject。
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 判断 Token 是否过期。
     */
    public boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getExpiration();
        return expiration == null || expiration.before(new Date());
    }

    private String normalizeToken(String token) {
        if (StrUtil.startWithIgnoreCase(token, "Bearer ")) {
            return token.substring(7);
        }
        return token;
    }
}
