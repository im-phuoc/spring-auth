package com.spring.springauth.models;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole name;

    public Role() {
    }

    public Role(ERole name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
