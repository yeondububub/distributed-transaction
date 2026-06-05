package com.example.sagaorchestrationproduct.controller.dto;

import com.example.sagaorchestrationproduct.application.dto.ProductBuyCancelCommand;

public record ProductBuyCancelRequest(String requestId) {

    public ProductBuyCancelCommand toCommand() {
        return new ProductBuyCancelCommand(requestId);
    }
}
