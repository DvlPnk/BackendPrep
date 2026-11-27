package com.pierre.inventory.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pending_cache_evictions")
public class PendingCacheEviction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PendingCacheEviction(){ }

    public PendingCacheEviction(Long productId){
        this.productId = productId;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
