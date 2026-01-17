package com.inhouse.iam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamConfig {
    @Bean
    public IamStore iamStore() {
        return new IamStore();
    }

    @Bean
    public TokenService tokenService() {
        return new TokenService();
    }
}
