package com.example.sagaorchestrationorder.controller.dto;

import com.example.sagaorchestrationorder.application.dto.CreateOrderCommand;

import java.util.List;

public record CreateOrderRequest(
    List<OrderItem> items
) {

    public CreateOrderCommand toCommand() {
        return new CreateOrderCommand(
                items.stream()
                        .map(item -> new CreateOrderCommand.OrderItem(item.productId, item.quantity))
                        .toList()
        );
    }

    public record OrderItem(
            Long productId,
            Long quantity
    ) {}
}
