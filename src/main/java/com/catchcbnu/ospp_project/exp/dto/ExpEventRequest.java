package com.catchcbnu.ospp_project.exp.dto;

public record ExpEventRequest(
        Long userId,
        String eventType,
        int amount
) {
}