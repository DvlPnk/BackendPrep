package com.pierre.orders.controller;

import com.pierre.orders.dto.CreateOrderRequest;
import com.pierre.orders.dto.OrderResponse;
import com.pierre.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service){
        this.service = service;
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
        OrderResponse order = service.createOrder(request);
        return ResponseEntity.created(URI.create("/orders/" + order.id())).body(order);
    }
}
