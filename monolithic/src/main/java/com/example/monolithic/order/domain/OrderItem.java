package com.example.monolithic.order.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long orderId;

    public Long productId;

    private Long quantity;

    public OrderItem() {
    }

    public OrderItem(Long orderId, Long productId, Long quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
