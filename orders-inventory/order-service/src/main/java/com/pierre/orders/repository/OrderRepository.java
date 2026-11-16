package com.pierre.orders.repository;

import com.pierre.orders.entity.Order;
import com.pierre.orders.enums.OrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findOrderById(long id);

    List<Order> findTop20ByStatusAndNextRetryAtLessThanEqualOrderByNextRetryAtAscIdAsc(
            OrderStatus status,
            Instant now
    );
}
