package com.catchcbnu.ospp_project.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 255, message = "이메일은 255자를 초과할 수 없습니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하여야 합니다.")
        String password,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
        String nickname,

        @NotBlank(message = "단과대는 필수입니다.")
        @Size(max = 100, message = "단과대는 100자를 초과할 수 없습니다.")
        String college,

        @NotBlank(message = "학과는 필수입니다.")
        @Size(max = 100, message = "학과는 100자를 초과할 수 없습니다.")
        String department
) {
}
