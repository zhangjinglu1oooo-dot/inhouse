package com.inhouse.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 应用注册服务配置。
 */
@Configuration
public class RegistryConfig {
    @Bean
    public AppStore appStore() {
        return new AppStore();
    }
}
