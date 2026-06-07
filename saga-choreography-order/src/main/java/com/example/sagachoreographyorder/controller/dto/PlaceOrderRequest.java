package com.example.sagachoreographyorder.controller.dto;

import com.example.sagachoreographyorder.application.dto.PlaceOrderCommand;

public record PlaceOrderRequest(Long orderId) {

    public PlaceOrderCommand toCommand() {
        return new PlaceOrderCommand(orderId);
    }
}
