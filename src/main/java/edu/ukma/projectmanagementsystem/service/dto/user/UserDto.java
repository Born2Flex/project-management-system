package edu.ukma.projectmanagementsystem.service.dto.user;

import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String username;

    private String name;

    private String email;

    private UserRole role;
}
