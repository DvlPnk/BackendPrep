package com.pierre.orders.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {
    private final RestClient client;

    public InventoryClient(
            @Value("${inventory.base-url}") String baseUrl
    ){
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void deductStock(long orderId, long productId, int quantity){
        client.post()
                .uri("/products/{id}/stock/deductions", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new StockDeductionRequest(orderId, quantity))
                .retrieve()
                .toBodilessEntity();
    }

    private record StockDeductionRequest(long orderId, int quantity) {
    }
}
