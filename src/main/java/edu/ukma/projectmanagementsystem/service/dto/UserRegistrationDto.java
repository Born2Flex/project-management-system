package edu.ukma.projectmanagementsystem.service.dto;

import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegistrationDto {
    private String username;

    private String email;

    private String password;

    private UserRole role;
}
