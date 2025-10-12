package edu.ukma.projectmanagementsystem.service.dto.user;

import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateDto {
    private String username;

    private String name;

    private String email;

    private UserRole role;
}
