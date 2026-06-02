package com.example.tccorder.infrastructure.point.dto;

public record PointReserveApiRequest(
        String requestId,
        Long userId,
        Long reserveAmount
) {
}
