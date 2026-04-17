package com.hnkjzy.ecg_collection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 参数配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 对称加密密钥（HS256 至少 32 字节）。
     */
    private String secretKey = "ecg_collection_demo_secret_key_32bytes_plus";

    /**
     * 过期时间（分钟）。
     */
    private Long expirationMinutes = 120L;

    /**
     * 签发方。
     */
    private String issuer = "ecg-collection";
}
