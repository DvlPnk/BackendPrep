package com.pierre.orders.retry;

import com.pierre.orders.service.OrderProcessingService;
import com.pierre.orders.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderRetryScheduler {
    private final OrderService orderService;
    private final OrderProcessingService processingService;

    public OrderRetryScheduler(OrderService orderService, OrderProcessingService processingService){
        this.orderService = orderService;
        this.processingService = processingService;
    }

    @Scheduled(fixedDelayString = "${orders.retry.delay-ms:5000}")
    public void retryPendingOrders(){
        for(long id : orderService.findDueRetryIds()){
            orderService.claimDueRetry(id)
                    .ifPresent(processingService::process);
        }
    }
}
