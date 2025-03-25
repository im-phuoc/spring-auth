package com.spring.springauth.services;

import com.spring.springauth.models.ERole;
import com.spring.springauth.models.Role;
import com.spring.springauth.models.User;
import com.spring.springauth.payload.request.UpdateRoleRequest;
import com.spring.springauth.payload.response.PagedResponse;
import com.spring.springauth.payload.response.UserResponse;
import com.spring.springauth.repository.RoleRepository;
import com.spring.springauth.repository.UserRepository;
import com.spring.springauth.security.services.UserDetailsImpl;
import com.spring.springauth.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("user","user@email.com","password");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(ERole.ROLE_USER));
        user.setRoles(roles);
    }

    @Test
    public void testGetAllUser_success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> usersList = List.of(user);
        Page<User> userPage = new PageImpl<>(usersList, pageable, usersList.size());
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        PagedResponse<UserResponse> response = userService.getAllUsers(0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        UserResponse userResponse = response.getContent().iterator().next();
        assertEquals("user", userResponse.getUsername());
        assertEquals("user@email.com", userResponse.getEmail());
        assertEquals(Set.of(ERole.ROLE_USER.name()), userResponse.getRoles());
        assertEquals(0, response.getPageNumber());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getTotalElements());
        assertTrue(response.isLastPage());
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetProfile_success() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl("user","user@email.com","password",Set.of(new SimpleGrantedAuthority(ERole.ROLE_USER.name())));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Act
        UserResponse userResponse = userService.getProfile();

        // Assert
        assertNotNull(userResponse);
        assertEquals("user", userResponse.getUsername());
        assertEquals("user@email.com", userResponse.getEmail());
        assertEquals(Set.of(ERole.ROLE_USER.name()), userResponse.getRoles());
    }

    @Test
    public void testGetUserByUsername_success() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Act
        UserResponse userResponse = userService.getUserByUsername("user");

        // Assert
        assertNotNull(userResponse);
        assertEquals("user", userResponse.getUsername());
        assertEquals("user@email.com", userResponse.getEmail());
        assertEquals(Set.of(ERole.ROLE_USER.name()), userResponse.getRoles());
        verify(userRepository, times(1)).findByUsername("user");
    }

    @Test
    public void testGetUserByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserByUsername("unknown"));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    public void testUpdateRoleByUsername_success() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Set<String> updateRoles = new HashSet<>(Set.of(ERole.ROLE_USER.name(),ERole.ROLE_ADMIN.name()));
        for (String role : updateRoles) {
            when(roleRepository.findByName(ERole.valueOf(role))).thenReturn(Optional.of(new Role(ERole.valueOf(role))));
        }
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(updateRoles);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserResponse userResponse  = userService.updateRoleByUsername("user",updateRoleRequest);

        // Assert
        assertNotNull(userResponse);
        assertEquals("user", userResponse.getUsername());
        assertEquals("user@email.com", userResponse.getEmail());
        assertEquals(Set.of(ERole.ROLE_ADMIN.name(),ERole.ROLE_USER.name()), userResponse.getRoles());
        verify(userRepository, times(1)).findByUsername("user");
        verify(roleRepository, times(2)).findByName(any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testUpdateRoleByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(Set.of(ERole.ROLE_USER.name()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateRoleByUsername("unknown",updateRoleRequest));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
        verify(roleRepository, never()).findByName(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testUpdateRoleByUsername_EmptyRole() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(Collections.emptySet());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateRoleByUsername("user",updateRoleRequest));
        assertEquals("Roles cannot be empty", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("user");
        verify(roleRepository, never()).findByName(any());
    }

    @Test
    public void testUpdateRoleByUsername_InvalidRole() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(Set.of("INVALID_ROLE"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateRoleByUsername("user",updateRoleRequest));

        assertEquals("Role 'INVALID_ROLE' is not a valid role", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("user");
    }

    @Test
    public void testUpdateRoleByUsername_RoleNotFound() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(roleRepository.findByName(ERole.ROLE_MODERATOR)).thenReturn(Optional.empty());
        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest(Set.of(ERole.ROLE_MODERATOR.name()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateRoleByUsername("user",updateRoleRequest));

        assertEquals("Role 'ROLE_MODERATOR' not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("user");
        verify(roleRepository, times(1)).findByName(ERole.ROLE_MODERATOR);
    }

    @Test
    public void testDeleteByUsername_success() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserByUsername("user");

        // Assert
        verify(userRepository, times(1)).findByUsername("user");
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteByUsername_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUserByUsername("unknown"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
        verify(userRepository,never()).delete(any());
    }

}
