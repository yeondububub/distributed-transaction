package com.example.tccorder.domain;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "orders")
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public Order() {
        this.status = OrderStatus.CREATED;
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
    }

    public enum OrderStatus {
        CREATED,
        COMPLETED,
        ;
    }
}