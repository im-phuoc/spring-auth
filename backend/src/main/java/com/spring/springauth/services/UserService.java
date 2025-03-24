package com.spring.springauth.services;

import com.spring.springauth.payload.request.UpdateRoleRequest;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;

import java.util.Set;

public interface UserService {
    PagedResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getProfile();
    UserResponse getUserByUsername(String username);
    UserResponse updateRoleByUsername(String username, UpdateRoleRequest updateRoleRequest);
    void deleteUserByUsername(String username);
}
