package com.example.tccproduct.controller.dto;

import com.example.tccproduct.application.dto.ProductReserveCancelCommand;

public record ProductReserveCancelRequest(String requestId) {

    public ProductReserveCancelCommand toCommand() {
        return new ProductReserveCancelCommand(requestId);
    }
}
