package com.inhouse.event;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {
    @Bean
    public EventStore eventStore() {
        return new EventStore();
    }
}
