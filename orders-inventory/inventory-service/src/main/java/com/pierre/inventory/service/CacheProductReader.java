package com.pierre.inventory.service;

import com.pierre.inventory.dto.ProductResponse;
import com.pierre.inventory.entity.Product;
import com.pierre.inventory.exception.ProductNotFoundException;
import com.pierre.inventory.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CacheProductReader {
    private final ProductRepository productRepository;

    public CacheProductReader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(cacheNames = "products", key = "#id")
    @Transactional(readOnly = true)
    public ProductResponse getById(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.isActive()
        );
    }
}
