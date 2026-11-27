package com.pierre.inventory.repository;

import com.pierre.inventory.entity.PendingCacheEviction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingCacheEvictionRepository extends JpaRepository<PendingCacheEviction, Long> {
    List<PendingCacheEviction> findTop50ByOrderByIdAsc();
}
