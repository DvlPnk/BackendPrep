package com.pierre.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_deductions")
public class StockDeduction {

    @Id
    @Column(name = "order_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    protected StockDeduction(){}

    public StockDeduction(long orderId, long productId, int quantity){
        this.id = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
