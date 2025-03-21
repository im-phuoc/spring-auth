package com.spring.springauth.services.impl;

import com.spring.springauth.models.User;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.repository.RoleRepository;
import com.spring.springauth.repository.UserRepository;
import com.spring.springauth.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        Set<UserResponse> responses = users.getContent().stream().map(
                user -> new UserResponse(
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet())
                )
        ).collect(Collectors.toSet());
        return new PagedResponse<>(responses,users.getNumber(),users.getSize(),users.getTotalPages(),users.getTotalElements(),users.isLast());
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return null;
    }

    @Override
    public UserResponse updateRoleByUsername(String username, Set<String> roles) {
        return null;
    }

    @Override
    public UserResponse deleteUserByUsername(String username) {
        return null;
    }
}
