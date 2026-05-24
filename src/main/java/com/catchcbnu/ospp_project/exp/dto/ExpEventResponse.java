package com.catchcbnu.ospp_project.exp.dto;

public record ExpEventResponse(
        Long userId,
        int level,
        int exp,
        String message
) {
}