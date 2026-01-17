package com.inhouse.permission;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限服务配置，注册内存存储 Bean。
 */
@Configuration
public class PermissionConfig {
    @Bean
    public PolicyStore policyStore() {
        return new PolicyStore();
    }
}
