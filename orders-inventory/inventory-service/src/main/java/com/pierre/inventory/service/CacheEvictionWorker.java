package com.pierre.inventory.service;

import com.pierre.inventory.entity.PendingCacheEviction;
import com.pierre.inventory.repository.PendingCacheEvictionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheEvictionWorker {
    private static final Logger log = LoggerFactory.getLogger(CacheEvictionWorker.class);

    private final PendingCacheEvictionRepository pendingCacheEvictionRepository;
    private final CacheManager cacheManager;

    public CacheEvictionWorker(PendingCacheEvictionRepository pendingCacheEvictionRepository,
                               CacheManager cacheManager){
        this.pendingCacheEvictionRepository = pendingCacheEvictionRepository;
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedDelayString = "${inventory.cache.eviction-delay-ms:5000}")
    public void processPendingEvictions(){
        Cache cache = cacheManager.getCache("products");

        if(cache == null){
            throw new IllegalStateException("Product cache is unavailable");
        }

        for(PendingCacheEviction eviction : pendingCacheEvictionRepository.findTop50ByOrderByIdAsc()){
            try{
                cache.evictIfPresent(eviction.getProductId());
                pendingCacheEvictionRepository.deleteById(eviction.getId());
            } catch(RuntimeException exception){
                log.warn("Could not complete cache eviction {}. Will retry",
                        eviction.getId(), exception);
                break;
            }
        }
    }
}
