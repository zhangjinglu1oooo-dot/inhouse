package com.inhouse.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryConfig {
    @Bean
    public AppStore appStore() {
        return new AppStore();
    }
}
