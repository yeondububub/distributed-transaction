package com.example.sagaorchestrationorder.controller.dto;

import com.example.sagaorchestrationorder.application.dto.PlaceOrderCommand;

public record PlaceOrderRequest(Long orderId) {

    public PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(orderId);
    }
}
