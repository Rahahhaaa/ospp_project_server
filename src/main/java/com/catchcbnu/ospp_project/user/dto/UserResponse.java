package com.catchcbnu.ospp_project.user.dto;

public record UserResponse(
        Long userId,
        String email,
        String nickname,
        String college,
        String department,
        int level,
        int exp
) {
}