package com.pierre.inventory.config;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;

public class RedisCacheErrorHandler extends SimpleCacheErrorHandler {
    private static final Logger log =
            LoggerFactory.getLogger(RedisCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(
            RuntimeException exception, Cache cache, Object key
    ) {
        if(exception instanceof RedisConnectionFailureException){
            log.warn("Redis unavailable when reading cache '{}', key '{}'. Using database.",
                    cache.getName(), key);
            return;
        }

        throw exception;
    }

    @Override
    public void handleCachePutError(
            RuntimeException exception, Cache cache, Object key, @Nullable Object value
    ) {
        if(exception instanceof RedisConnectionFailureException){
            log.warn("Redis unavailable when writing cache '{}', key '{}'. Skipping cache write.",
                    cache.getName(), key);
            return;
        }

        throw exception;
    }
}
