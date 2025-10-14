package edu.ukma.projectmanagementsystem.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email",  nullable = false, unique = true)
    private String email;

    @Column(name = "password",  nullable = false)
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id",  nullable = false)
    private RoleEntity role;

    @ManyToMany(mappedBy = "developers", fetch = FetchType.LAZY)
    private Set<ProjectEntity> projects = new HashSet<>();
}
