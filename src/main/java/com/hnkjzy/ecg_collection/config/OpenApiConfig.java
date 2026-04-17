package com.hnkjzy.ecg_collection.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / OpenAPI 配置。
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ecgOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECG Collection API")
                        .description("心电图情报分析系统后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("ECG Team").email("dev@hnkjzy.com"))
                        .license(new License().name("Internal Use").url("https://example.com")));
    }
}
