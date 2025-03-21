package com.spring.springauth.services.impl;

import com.spring.springauth.models.ERole;
import com.spring.springauth.models.Role;
import com.spring.springauth.models.User;
import com.spring.springauth.payload.request.UpdateRoleRequest;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.repository.RoleRepository;
import com.spring.springauth.repository.UserRepository;
import com.spring.springauth.security.services.UserDetailsImpl;
import com.spring.springauth.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
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
    public UserResponse getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new UserResponse(userDetails.getUsername(),userDetails.getEmail(),userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    @Override
    public UserResponse getUserByUsername(String username) {
        User user = findByUsername(username);
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet())
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse updateRoleByUsername(String username, UpdateRoleRequest updateRoleRequest) {
        User user = findByUsername(username);
        Set<String> updatedRoles = updateRoleRequest.getRoles();
        if (updatedRoles == null || updatedRoles.isEmpty()) {
            throw new RuntimeException("Roles cannot be empty");
        }
        Set<Role> roles = new HashSet<>();
        for (String roleName : updatedRoles) {
            ERole eRole;
            try {
                eRole = ERole.valueOf(roleName);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Role '" + roleName + "' is not a valid role");
            }
            Role role = roleRepository.findByName(eRole).orElseThrow(
                    () -> new RuntimeException("Role '" + roleName + "' not found")
            );
            roles.add(role);
        }
        user.setRoles(roles);

        User updatedUser = userRepository.save(user);
        return new UserResponse(updatedUser.getUsername(),updatedUser.getEmail(),updatedUser.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()));
    }


    @Override
    public void deleteUserByUsername(String username) {
        User user = findByUsername(username);
        userRepository.delete(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new RuntimeException("User not found")
        );
    }
}
