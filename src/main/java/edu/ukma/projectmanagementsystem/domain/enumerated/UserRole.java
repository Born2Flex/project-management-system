package edu.ukma.projectmanagementsystem.domain.enumerated;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    PROJECT_MANAGER,
    USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
