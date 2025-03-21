package com.spring.springauth.controllers;

import com.spring.springauth.payload.request.LoginRequest;
import com.spring.springauth.payload.request.RegisterRequest;
import com.spring.springauth.payload.response.ApiResponse;
import com.spring.springauth.payload.response.LoginResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new ApiResponse<>(loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        UserResponse userResponse = authService.registerUser(registerRequest);
        return ResponseEntity.ok(new ApiResponse<>(userResponse));
    }
}
