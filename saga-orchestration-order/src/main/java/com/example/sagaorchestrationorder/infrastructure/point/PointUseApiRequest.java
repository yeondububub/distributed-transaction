package com.example.sagaorchestrationorder.infrastructure.point;

public record PointUseApiRequest(
        String requestId,
        Long userId,
        Long amount
) {}
