package com.example.sagachoreographypoint.consumer.dto;

public record QuantityDecreasedEvent(
        Long orderId,
        Long totalPrice
) {
}
