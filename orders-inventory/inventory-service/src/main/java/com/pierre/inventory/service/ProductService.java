package com.pierre.inventory.service;

import com.pierre.inventory.contracts.InventoryErrorCode;
import com.pierre.inventory.dto.*;
import com.pierre.inventory.entity.Product;
import com.pierre.inventory.entity.StockDeduction;
import com.pierre.inventory.exception.IdempotencyKeyMismatchException;
import com.pierre.inventory.exception.ProductNotFoundException;
import com.pierre.inventory.exception.StockDeductionException;
import com.pierre.inventory.repository.ProductRepository;
import com.pierre.inventory.repository.StockDeductionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    private final StockDeductionRepository stockRepository;

    private final EntityManager entityManager;

    public ProductService(ProductRepository productRepository,
                          StockDeductionRepository stockRepository,
                          EntityManager entityManager){
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request){
        Product product = new Product(
                request.name().trim(),
                request.price(),
                request.stock());

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(long id){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id)
        );
        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.isActive()
        );
    }

    @Transactional
    public ProductResponse updatePrice(long id, UpdateProductPriceRequest request){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id)
        );
        product.updatePrice(request.price());
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductPageResponse getProducts(Boolean active, int page, int size){
       if(page < 0){
           throw new IllegalArgumentException("Page must be zero or greater");
       }

       if(size < 1 || size > 100){
           throw new IllegalArgumentException("Size must be between 1 and 100");
       }

       Pageable pageable = PageRequest.of(
               page,
               size,
               Sort.by("id").ascending()
       );

       Page<Product> products;

       if(active == null){
           products = productRepository.findAll(pageable);
       }
       else{
           products = productRepository.findByActive(active, pageable);
       }

       List<ProductResponse> items = products.getContent()
               .stream()
               .map(this::toResponse)
               .toList();

       return new ProductPageResponse(
               items,
               products.getNumber(),
               products.getSize(),
               products.getTotalElements(),
               products.getTotalPages()
       );
    }

    @Transactional
    public ProductResponse updateActive(long id, UpdateProductActiveRequest request){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id)
        );
        product.setActive(request.active());

        return toResponse(product);
    }

    @Transactional
    public ProductResponse updateStock(long id, UpdateProductStockRequest request){
        Optional<StockDeduction> foundDeduction = stockRepository.findById(request.orderId());
        int quantity = request.quantity();

        if(foundDeduction.isPresent()){
            if(quantity ==  foundDeduction.get().getQuantity()
                    && id == foundDeduction.get().getProductId()){
                Product product = productRepository.findById(id).orElseThrow(
                        () -> new ProductNotFoundException(id)
                );
                return toResponse(product);
            }
            else{
                throw new IdempotencyKeyMismatchException(request.orderId());
            }
        }

        Product product = productRepository.findById(id).orElseThrow(
                () -> new ProductNotFoundException(id)
        );

        if(!product.isActive()){
            throw new StockDeductionException(InventoryErrorCode.PRODUCT_INACTIVE.name(), "Product is not active");
        }
        if(product.getStock() < quantity){
            throw new StockDeductionException(InventoryErrorCode.INSUFFICIENT_STOCK.name(), "Not enough items in stock");
        }
        product.deductStock(quantity);

        entityManager.persist(new StockDeduction(request.orderId(), id, quantity));
        return toResponse(product);
    }
}
