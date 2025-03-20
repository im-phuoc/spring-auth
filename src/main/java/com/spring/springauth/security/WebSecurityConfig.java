package com.spring.springauth.security;

import com.spring.springauth.models.ERole;
import com.spring.springauth.models.Role;
import com.spring.springauth.models.User;
import com.spring.springauth.repository.RoleRepository;
import com.spring.springauth.repository.UserRepository;
import com.spring.springauth.security.jwt.AuthEntryPoint;
import com.spring.springauth.security.jwt.AuthTokenFilter;
import com.spring.springauth.security.services.UserDetailsServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPoint unauthorizedHandler;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPoint unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated());
        http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.authenticationProvider(authProvider());
        return http.build();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(ERole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role moderatorRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(ERole.ROLE_MODERATOR);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(ERole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@gmail.com", passwordEncoder().encode("123456"));
                userRepository.save(admin);
            }

            userRepository.findByUsername("admin").ifPresent(admin -> {
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                roles.add(moderatorRole);
                roles.add(adminRole);
                admin.setRoles(roles);
                userRepository.save(admin);
            });


        };
    }
}
