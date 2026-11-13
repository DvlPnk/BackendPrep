package com.pierre.orders.controller;

import com.pierre.orders.client.InventoryClient;
import com.pierre.orders.dto.CreateOrderRequest;
import com.pierre.orders.dto.OrderResponse;
import com.pierre.orders.enums.OrderStatus;
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

    public OrderController(OrderService service, InventoryClient inventoryClient){
        this.service = service;
        this.inventoryClient = inventoryClient;
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

        try{
            inventoryClient.deductStock(pending.id(), pending.productId(), pending.quantity());
        }
        catch (RestClientResponseException exception) {
            Object code = null;
            try {
                ProblemDetail problem = exception.getResponseBodyAs(ProblemDetail.class);
                if (problem != null && problem.getProperties() != null) {
                    code = problem.getProperties().get("code");
                }
            } catch (RestClientException decodingFailure) {
                // Inventory returned an error body we couldn't read.
            }

            if ("PRODUCT_NOT_FOUND".equals(code)
                    || "PRODUCT_INACTIVE".equals(code)
                    || "INSUFFICIENT_STOCK".equals(code)) {
                OrderResponse rejected =
                        service.updateStatus(pending.id(), OrderStatus.REJECTED);
                return ResponseEntity.created(location).body(rejected);
            }

            if ("IDEMPOTENCY_KEY_MISMATCH".equals(code)) {
                // Log pending.id() for investigation.
            }

            return ResponseEntity.accepted().location(location).body(pending);
        }
        catch (RestClientException exception){
            return ResponseEntity.accepted().location(location).body(pending);
        }

        OrderResponse ready = service.updateStatus(pending.id(), OrderStatus.READY);

        return ResponseEntity.created(location).body(ready);
    }
}
