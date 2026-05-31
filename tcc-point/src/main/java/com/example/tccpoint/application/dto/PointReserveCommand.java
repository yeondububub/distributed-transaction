package com.example.tccpoint.application.dto;

public record PointReserveCommand(
        String requestId,
        Long userId,
        Long reserveAmount
) {
}
