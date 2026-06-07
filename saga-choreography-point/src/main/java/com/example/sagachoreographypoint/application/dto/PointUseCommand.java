package com.example.sagachoreographypoint.application.dto;

public record PointUseCommand(
        String requestId,
        Long userId,
        Long amount
) { }