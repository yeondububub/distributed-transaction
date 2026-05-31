package com.example.tccpoint.controller.dto;

import com.example.tccpoint.application.dto.PointReserveCommand;

public record PointReserveRequest(
        String requestId,
        Long userId,
        Long reserveAmount
) {

    public PointReserveCommand toCommand() {
        return new PointReserveCommand(requestId, userId, reserveAmount);
    }
}
