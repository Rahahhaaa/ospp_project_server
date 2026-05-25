package com.catchcbnu.ospp_project.auth.controller;

import com.catchcbnu.ospp_project.auth.dto.SignupRequest;
import com.catchcbnu.ospp_project.auth.dto.SignupResponse;
import com.catchcbnu.ospp_project.auth.service.AuthService;
import com.catchcbnu.ospp_project.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, "회원가입 성공", response));
    }
}
