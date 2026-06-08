package com.example.sagachoreographyproduct.infrastructure.kafka.dto;

public record QuantityDecreasedEvent(Long orderId, Long totalPrice) {
}
