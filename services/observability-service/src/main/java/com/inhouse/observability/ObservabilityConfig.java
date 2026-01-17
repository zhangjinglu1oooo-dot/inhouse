package com.inhouse.observability;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 可观测服务配置。
 */
@Configuration
public class ObservabilityConfig {
    @Bean
    public AuditStore auditStore() {
        return new AuditStore();
    }
}
