package com.spring.springauth.controllers;

import com.spring.springauth.payload.response.ApiResponse;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<UserResponse>>> getAllUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok().body(new ApiResponse<>(userService.getAllUsers(page, size)));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return null;
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserResponse> updateRoleByUsername(@PathVariable String username) {
        return null;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<UserResponse> deleteUserByUsername(@PathVariable String username) {
        return null;
    }
}
