package com.pierre.inventory.controller;

import com.pierre.inventory.dto.*;
import com.pierre.inventory.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service){
        this.service = service;
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable("id") long id
    ){
        return service.getProductById(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request
            ){
        ProductResponse product = service.createProduct(request);
        return ResponseEntity
                .created(URI.create("/products/" + product.id()))
                .body(product);
    }

    @PatchMapping("/{id}/price")
    public ResponseEntity<ProductResponse> updateProductPrice(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateProductPriceRequest request
            ){
        return ResponseEntity.ok(service.updatePrice(id, request));
    }

    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(service.getProducts(active, page, size));
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductResponse> updateProductActive(
            @PathVariable("id") long id,
            @Valid @RequestBody UpdateProductActiveRequest request
            ){
        return ResponseEntity.ok(service.updateActive(id, request));
    }
}
