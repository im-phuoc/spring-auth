package com.spring.springauth.services;

import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;

import java.util.Set;

public interface UserService {
    PagedResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserByUsername(String username);
    UserResponse updateRoleByUsername(String username, Set<String> roles);
    UserResponse deleteUserByUsername(String username);
}
