package com.example.sagachoreographyorder.infrastructure.point;

public record PointUseApiRequest(
        String requestId,
        Long userId,
        Long amount
) {}
