package com.example.sagachoreographyproduct.controller.dto;

import com.example.sagachoreographyproduct.application.dto.ProductBuyCancelCommand;

public record ProductBuyCancelRequest(String requestId) {

    public ProductBuyCancelCommand toCommand() {
        return new ProductBuyCancelCommand(requestId);
    }
}
