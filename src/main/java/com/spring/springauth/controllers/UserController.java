package com.spring.springauth.controllers;

import com.spring.springauth.payload.request.UpdateRoleRequest;
import com.spring.springauth.payload.response.ApiResponse;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.services.UserService;
import jakarta.validation.Valid;
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

    @GetMapping("/intro")
    public ResponseEntity<ApiResponse<UserResponse>> introSpec() {
        return ResponseEntity.ok().body(new ApiResponse<>(userService.getProfile()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok().body(new ApiResponse<>(userService.getUserByUsername(username)));
    }

    @PutMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> updateRoleByUsername(@PathVariable String username, @RequestBody @Valid UpdateRoleRequest updateRoleRequest) {
        return ResponseEntity.ok().body(new ApiResponse<>(userService.updateRoleByUsername(username, updateRoleRequest)));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<ApiResponse<String>> deleteUserByUsername(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok().body(new ApiResponse<>("User with username '" + username + "' has been deleted successfully"));
    }
}
