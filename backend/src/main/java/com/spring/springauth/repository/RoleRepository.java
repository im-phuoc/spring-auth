package com.spring.springauth.repository;

import com.spring.springauth.models.ERole;
import com.spring.springauth.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
