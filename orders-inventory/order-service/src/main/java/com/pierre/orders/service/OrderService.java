package com.pierre.orders.service;

import com.pierre.orders.dto.CreateOrderRequest;
import com.pierre.orders.dto.OrderResponse;
import com.pierre.orders.entity.Order;
import com.pierre.orders.exceptions.OrderNotFoundException;
import com.pierre.orders.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository repository;

    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public OrderResponse createOrder(CreateOrderRequest request){
        Order order = new Order(
            request.productId(),
            request.quantity()
        );
        return toResponse(repository.save(order));
    }

    public OrderResponse toResponse(Order order){
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }

    public OrderResponse getOrderById(long id){
        Order order = repository.findById(id).orElseThrow(
                () -> new OrderNotFoundException(id)
        );
        return toResponse(order);
    }
}
