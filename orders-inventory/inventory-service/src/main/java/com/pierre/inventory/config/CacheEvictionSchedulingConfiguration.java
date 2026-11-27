package com.pierre.inventory.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        name = "inventory.cache-eviction.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class CacheEvictionSchedulingConfiguration {
}
