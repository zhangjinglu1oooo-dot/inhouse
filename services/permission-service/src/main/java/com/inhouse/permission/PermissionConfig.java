package com.inhouse.permission;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionConfig {
    @Bean
    public PolicyStore policyStore() {
        return new PolicyStore();
    }
}
