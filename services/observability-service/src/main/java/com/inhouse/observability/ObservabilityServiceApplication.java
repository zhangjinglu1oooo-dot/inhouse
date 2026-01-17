package com.inhouse.observability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 可观测服务启动类。
 */
@SpringBootApplication
public class ObservabilityServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ObservabilityServiceApplication.class, args);
    }
}
