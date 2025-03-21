package com.spring.springauth.services;

import com.spring.springauth.payload.request.LoginRequest;
import com.spring.springauth.payload.request.RegisterRequest;
import com.spring.springauth.payload.response.LoginResponse;
import com.spring.springauth.payload.response.UserResponse;

public interface AuthService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
    UserResponse registerUser(RegisterRequest registerRequest);
}
