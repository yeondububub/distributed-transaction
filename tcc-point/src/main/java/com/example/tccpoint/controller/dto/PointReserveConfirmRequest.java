package com.example.tccpoint.controller.dto;

import com.example.tccpoint.application.dto.PointReserveConfirmCommand;

public record PointReserveConfirmRequest(String requestId) {

    public PointReserveConfirmCommand toCommand() {
        return new PointReserveConfirmCommand(requestId);
    }
}
