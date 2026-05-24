package com.catchcbnu.ospp_project.user.dto;

public record SignupRequest(
        String email,
        String password,
        String nickname,
        String college,
        String department
) {
}