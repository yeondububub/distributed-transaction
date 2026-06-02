package com.example.tccpoint.controller.dto;

import com.example.tccpoint.application.dto.PointReserveCancelCommand;

public record PointReserveCancelRequest(String requestId) {

    public PointReserveCancelCommand toCommand() {
        return new PointReserveCancelCommand(requestId);
    }
}
