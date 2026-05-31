package com.example.tccproduct.controller.dto;

import com.example.tccproduct.application.dto.ProductReserveConfirmCommand;

public record ProductReserveConfirmRequest(String requestId) {

    public ProductReserveConfirmCommand toCommand() {
        return new ProductReserveConfirmCommand(requestId);
    }
}
