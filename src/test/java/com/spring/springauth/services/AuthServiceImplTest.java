package com.spring.springauth.services;

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
import com.spring.springauth.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("username");
        loginRequest.setPassword("password");
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("username");
        registerRequest.setEmail("user@example.com");
        registerRequest.setPassword("password");
    }

    @Test
    public void testAuthenticateUser_success() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(true);
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl("username","user@user.com","123456", Set.of(new SimpleGrantedAuthority(ERole.ROLE_USER.name())));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateToken(authentication)).thenReturn("token");

        // Act
        LoginResponse response = authService.authenticateUser(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("token", response.getToken());
        assertEquals("username", response.getUsername());
        assertEquals("user@user.com", response.getEmail());
        assertEquals(Set.of(ERole.ROLE_USER.name()), response.getRoles());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateToken(authentication);
    }

    @Test
    public void testAuthenticateUser_UserNotFound() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(false);
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticateUser(loginRequest));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    public void testAuthenticateUser_BadCredentials() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Incorrect username or password"));
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.authenticateUser(loginRequest));
        assertEquals("Incorrect username or password", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, never()).generateToken(any());
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        Role userRole = new Role(ERole.ROLE_USER);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RegisterResponse response = authService.registerUser(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("username", response.getUsername());
        assertEquals("user@example.com", response.getEmail());
        assertEquals(Set.of(ERole.ROLE_USER.name()), response.getRoles());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, times(1)).existsByEmail("user@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyInUse() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registerRequest);
        });
        assertEquals("Username is already in use", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testRegisterUser_EmailAlreadyInUse() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registerRequest);
        });
        assertEquals("Email is already in use", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, times(1)).existsByEmail("user@example.com");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testRegisterUser_RoleNotFound() {
        // Arrange
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registerRequest);
        });
        assertEquals("Role not found", exception.getMessage());
        verify(userRepository, times(1)).existsByUsername("username");
        verify(userRepository, times(1)).existsByEmail("user@example.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_USER);
        verify(userRepository, never()).save(any());
    }
}
