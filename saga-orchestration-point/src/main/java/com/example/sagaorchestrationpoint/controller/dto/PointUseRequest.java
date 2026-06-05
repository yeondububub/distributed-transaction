package com.example.sagaorchestrationpoint.controller.dto;

import com.example.sagaorchestrationpoint.application.dto.PointUseCommand;

public record PointUseRequest(
        String requestId,
        Long userId,
        Long amount
) {

    public PointUseCommand toCommand() {
        return new PointUseCommand(requestId, userId, amount);
    }
}
