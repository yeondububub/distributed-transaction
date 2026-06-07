package com.example.sagachoreographypoint.controller.dto;

import com.example.sagachoreographypoint.application.dto.PointUseCancelCommand;

public record PointUseCancelRequest(String requestId) {

    public PointUseCancelCommand toCommand() {
        return new PointUseCancelCommand(requestId);
    }
}
