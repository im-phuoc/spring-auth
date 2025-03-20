package com.spring.springauth.services.impl;

import com.spring.springauth.models.ERole;
import com.spring.springauth.models.Role;
import com.spring.springauth.models.User;
import com.spring.springauth.payload.request.LoginRequest;
import com.spring.springauth.payload.request.RegisterRequest;
import com.spring.springauth.payload.response.LoginResponse;
import com.spring.springauth.payload.response.RegisterResponse;
import com.spring.springauth.repository.RoleRepository;
import com.spring.springauth.repository.UserRepository;
import com.spring.springauth.security.jwt.JwtUtils;
import com.spring.springauth.security.services.UserDetailsImpl;
import com.spring.springauth.services.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        if (!userRepository.existsByUsername(loginRequest.getUsername())) {
            throw new RuntimeException("User not found");
        }
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            return new LoginResponse(jwt,userDetails.getUsername(),userDetails.getEmail(),roles);
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Incorrect username or password");
        }
    }

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already in use");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), passwordEncoder.encode(registerRequest.getPassword()));
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return new RegisterResponse(user.getUsername(),user.getEmail(),user.getRoles().stream().map(role->role.getName().name()).collect(Collectors.toSet()));
    }
}
