package com.example.sagaorchestrationorder.application.dto;

import java.util.List;

public record CreateOrderCommand(
        List<OrderItem> items
) {

    public record OrderItem(
            Long productId,
            Long quantity
    ) {
    }

}
