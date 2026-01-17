package com.inhouse.observability;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {
    @Bean
    public AuditStore auditStore() {
        return new AuditStore();
    }
}
