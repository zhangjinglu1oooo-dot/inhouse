package com.inhouse.iam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * IAM 服务配置。
 */
@Configuration
public class IamConfig {
    @Bean
    public TokenService tokenService(StringRedisTemplate stringRedisTemplate) {
        return new TokenService(stringRedisTemplate);
    }
}
