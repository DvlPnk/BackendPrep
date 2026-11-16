package com.pierre.orders.controller;

import com.pierre.orders.client.InventoryClient;
import com.pierre.orders.dto.CreateOrderRequest;
import com.pierre.orders.dto.OrderResponse;
import com.pierre.orders.enums.OrderStatus;
import com.pierre.orders.service.OrderProcessingService;
import com.pierre.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;
    private final InventoryClient inventoryClient;

    private final OrderProcessingService processingService;

    public OrderController(OrderService service, InventoryClient inventoryClient,
                           OrderProcessingService processingService){
        this.service = service;
        this.inventoryClient = inventoryClient;
        this.processingService = processingService;
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @PathVariable("id") long id
    ){
        return service.getOrderById(id);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request
            ){
        OrderResponse pending = service.createOrder(request);
        URI location = URI.create("/orders/" + pending.id());

        OrderResponse result = processingService.process(pending);

        if (result.status() == OrderStatus.PENDING) {
            return ResponseEntity.accepted().location(location).body(result);
        }

        return ResponseEntity.created(location).body(result);
    }

    @PostMapping("/{id}/retry")
    public ResponseEntity<OrderResponse> retryOrder(
            @PathVariable("id") long id){
        OrderResponse order = service.getOrderById(id);
        OrderResponse result = processingService.process(order);

        if (result.status() == OrderStatus.PENDING) {
            return ResponseEntity.accepted().body(result);
        }

        return ResponseEntity.ok(result);
    }
}
