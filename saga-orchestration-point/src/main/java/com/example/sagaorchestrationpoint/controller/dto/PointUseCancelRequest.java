package com.example.sagaorchestrationpoint.controller.dto;

import com.example.sagaorchestrationpoint.application.dto.PointUseCancelCommand;

public record PointUseCancelRequest(String requestId) {

    public PointUseCancelCommand toCommand() {
        return new PointUseCancelCommand(requestId);
    }
}
