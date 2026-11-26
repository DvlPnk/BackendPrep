package com.pierre.inventory.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RedisCacheErrorHandlerTest {

    @Test
    void redisConnectionFailureDuringGetDoesNotThrow(){
        RedisCacheErrorHandler handler = new RedisCacheErrorHandler();
        Cache cache = new ConcurrentMapCache("products");

        assertDoesNotThrow(() ->
                handler.handleCacheGetError(
                        new RedisConnectionFailureException("Redis unavailable"),
                        cache
                        , 7L
                ));
    }

    @Test
    void redisConnectionFailureDuringPutDoesNotThrow(){
        RedisCacheErrorHandler handler = new RedisCacheErrorHandler();
        Cache cache = new ConcurrentMapCache("products");

        assertDoesNotThrow(() ->
                handler.handleCachePutError(
                        new RedisConnectionFailureException("Redis unavailable"),
                        cache,
                        7L,
                        null
                ));
    }

    @Test
    void  unrelatedErrorDuringGetIsRethrown(){
        RedisCacheErrorHandler handler = new RedisCacheErrorHandler();
        Cache cache = new ConcurrentMapCache("products");

        assertThrows(IllegalStateException.class, () ->
                handler.handleCacheGetError(
                        new IllegalStateException("Some other error"),
                        cache,
                        7L
                ));
    }
}
