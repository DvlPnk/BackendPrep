package com.pierre.orders.service;

import com.pierre.orders.client.InventoryClient;
import com.pierre.orders.dto.OrderResponse;
import com.pierre.orders.enums.OrderStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import com.pierre.inventory.contracts.InventoryErrorCode;

import java.net.URI;

@Service
public class OrderProcessingService {
    private final InventoryClient inventoryClient;
    private final OrderService orderService;

    public OrderProcessingService(InventoryClient inventoryClient, OrderService orderService){
        this.inventoryClient = inventoryClient;
        this.orderService = orderService;
    }
    public OrderResponse process(OrderResponse order){
        if(order.status() != OrderStatus.PENDING){
            return order;
        }

        URI location = URI.create("/orders/" + order.id());

        try{
            inventoryClient.deductStock(order.id(), order.productId(), order.quantity());
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

            if (InventoryErrorCode.PRODUCT_NOT_FOUND.name().equals(code)
                    || InventoryErrorCode.PRODUCT_INACTIVE.name().equals(code)
                    || InventoryErrorCode.INSUFFICIENT_STOCK.name().equals(code)) {
                return orderService.updateStatus(order.id(), OrderStatus.REJECTED);
            }

            if (InventoryErrorCode.IDEMPOTENCY_KEY_MISMATCH.name().equals(code)) {
                return orderService.updateStatus(order.id(), OrderStatus.NEEDS_REVIEW);
            }

            return order;
        }
        catch (RestClientException exception){
            return order;
        }

        return orderService.updateStatus(order.id(), OrderStatus.READY);
    }
}
